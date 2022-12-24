import java.io.*;
import java.nio.file.Paths;
import java.util.*;
import java.nio.file.Files;

public class Main {
    public static void main(String[] args) throws IOException {

        int choice;
        System.out.println("\n          Добро пожаловать в ресторан!");
        System.out.println("\n***************************************************");
        System.out.println("*                  Меню                           *");
        System.out.println("*    1. Список свободных столов                   *");
        System.out.println("*    2. Добавить блюдо в заказ                    *");
        System.out.println("*    3. Расчитать стол                            *");
        System.out.println("*    4. Полный список заказа                        *");
        System.out.println("*    0. Выход из программы                        *");
        System.out.println("***************************************************");

        choice = Keyin.inInt(" Выберите пункт : ");
        switch (choice) {
            case 1:
                System.out.println("Список свободных столов");
                TM_list tm_list = new TM_list();
                DishManager tm = new DishManager(tm_list);
                tm.start();
                break;
            case 2:
                System.out.println("Добавить блюдо в заказ");
                TM_list.DishAdd();
                break;
            case 3:
                System.out.println("Рассчитать стол");
                TM_list.payment();
                break;
            case 4:
                System.out.println("Полный список заказа");
                SavedOutput.readSavedList();
                break;
            case 0:
                System.out.println("Выход из программы");
                System.exit(0);
            default:
                System.out.println("Такого пункта не существует! ");
                break;
        }


    }
}
class Keyin { // Класс, методы которого обрабатывают корректность ввода данных в меню
    public static void printPrompt(String prompt) {
        System.out.print(prompt + " ");
        System.out.flush(); // метод flush выбрасывает все из буфера в поток вывода System.out
    }

    //Метод, позволяющий убедиться в том, что в буфере потока ввода не осталось данных
    public static void inputFlush() {
        int opt;

        try {
            while ((System.in.available()) != 0)
                opt = System.in.read();
        } catch (java.io.IOException e) {
            System.out.println("Ошибка ввода. Введите число, соответствующее варианту меню");
        }
    }
    public static String inString() {
        int aChar;
        String s = "";
        boolean finished = false;

        while (!finished) {
            try {
                aChar = System.in.read();
                if (aChar < 0 || (char) aChar == '\n')
                    finished = true;
                else if ((char) aChar != '\r')
                    s = s + (char) aChar; // Enter into string
            }

            catch (java.io.IOException e) {
                System.out.println("Ошибка ввода. Введите число, соответствующее варианту меню");
                finished = true;
            }
        }
        return s;
    }

    public static int inInt(String prompt) {
        while (true) {
            inputFlush();
            printPrompt(prompt);
            try {
                return Integer.parseInt(inString().trim());
            }

            catch (NumberFormatException e) {
                System.out.println("Неверный ввод. Нужно число");
            }
        }
    }
}
class DishManager { // Класс, обрабатывающий и перенимающий методы из класса TM_list
    private boolean app_status = true;
    public static Scanner sc = new Scanner(System.in);
    private final TM_list tm_list;

    public DishManager(TM_list tm_list){
        this.tm_list = tm_list;
    }

    public void start(){
        mainLoop();
        Input.writeFullList();
        SavedOutput.readSavedList();
    }

    private void mainLoop(){ // Основной механизм работы, основанный на булевой переменной (статус)
        while(app_status){
            printEnterLine();
            String command = readCommand();
            app_status = executeCommand(command);
        }
    }

    private boolean executeCommand(String command){ //Добавляет команду в список или получает команду выйти и открыть список
        if (observeList(command)) {
            tm_list.printList();
            return false;
        }
        else {
            tm_list.addToList(command);
            return true;
        }
    }

    public void printEnterLine(){
        System.out.print("---->");
    }

    private String readCommand(){
        return sc.nextLine();
    }

}

class TM_list {
    public static Scanner tm_scan = new Scanner(System.in);// Для изменения данных / добавления

    // Структура использования списков - информация приходит в tm_list, далее сплитуется и информация до ":" попадает в name_list - это название блюда

    private final ArrayList<String> tm_list = new ArrayList<>(); //Изначальный список, куда приходит инфа из Scanner
    public static ArrayList<String> name_list = new ArrayList<>(); // Список по именам, отспличенный по ":"
    public static ArrayList<String> Costs = new ArrayList<>(); //Список, куда вносится цена


// Списки static, так как используются в методе, записывающем их содержимое в файл

    public static void DishAdd() throws IOException { // добавление блюда через промежуточный список
        //Конструкция, считывающая строки файла 1 строка = 1 список
        String line1 = Files.readAllLines(Paths.get("zakaz.txt")).get(0);
        String line2 = Files.readAllLines(Paths.get("zakaz.txt")).get(1);


        List<String> DishName = new ArrayList<>(Arrays.asList(line1.split(","))); //
        List<String> Costs = new ArrayList<>(Arrays.asList(line2.split(","))); //

        System.out.println("Добавьте название блюда: ");
        System.out.print("---->");
        String name = tm_scan.nextLine();
        DishName.add(0, name);

        System.out.println("Добавлено блюдо: " + DishName.get(0));
        System.out.println("Добавлена цена : " + Costs.get(0));

        String list1 = Arrays.toString(DishName.toArray()).replace("[", "").replace("]", "");
        String list2 = Arrays.toString(Costs.toArray()).replace("[", "").replace("]", "");

        // Вносятся изменения в финальные списки, откуда инфа записывается в текстовый файл
        name_list.add(list1);
        Costs.add(list2);
        Input.writeFullList();
    }
    public static void payment() throws IOException{
        //Конструкция, считывающая строки файла 1 строка = 1 список
        String line1 = Files.readAllLines(Paths.get("tasks.txt")).get(0);
        String line2 = Files.readAllLines(Paths.get("tasks.txt")).get(1);

        List<String> DishName = new ArrayList<>(Arrays.asList(line1.split(","))); //
        List<String> Costs = new ArrayList<>(Arrays.asList(line2.split(","))); //

        System.out.println("Название: " + DishName);
        System.out.println("Цена: " + Costs);

        System.out.println("\nНазвание: " + DishName);
        System.out.println("Цена: " + Costs);

        String list1 = Arrays.toString(DishName.toArray()).replace("[", "").replace("]", "");
        String list2 = Arrays.toString(Costs.toArray()).replace("[", "").replace("]", "");

        // Вносятся изменения в финальные списки, откуда инфа записывается в текстовый файл
        name_list.add(list1);
        Costs.add(list2);
        Input.writeFullList();

    }
}
class Input{ // Здесь сохраняются основные списки в файл
    // Для сохранения данных полный список full_list выводится в текстовый файл
    // Значение append = false => файл будет перезаписываться
    public static void writeFullList() {
        File myfile = new File("C://Users//216895//IdeaProjects//zachet//zakaz.txt");
        if (myfile.exists()){
            System.out.println("File exists");
        }
        else {
            System.out.println("Not found");}
        try(FileWriter writer = new FileWriter("zakaz.txt", false)){
            //Убирает квадратные скобки, но меняет тип на строковый => не работает по индексам


            String text2 = String.valueOf(TM_list.name_list);
            String text3 = String.valueOf(TM_list.Costs);

            writer.write(text2);
            writer.append("\n");
            writer.write(text3);
            writer.append("\n");
            writer.flush();
        }
        catch (IOException ex){
            System.out.println(ex.getMessage());
        }
    }
    class SavedOutput{ // Вывод сохраненных списков в консоль
        public static void readSavedList(){

            try(FileReader reader = new FileReader("zakaz.txt")){
                BufferedReader br = new BufferedReader(new FileReader("zakaz.txt"));
                if (br.readLine() == null) {
                    System.out.println("\nВ файле ничего не было сохранено. ");
                }
                char[] buf = new char[999];
                int c;
                while((c = reader.read(buf))>0){
                    if(c < 999){
                        buf = Arrays.copyOf(buf, c);
                    }
                    System.out.print(buf);
                }
            }
            catch(IOException ex){
                System.out.println(ex.getMessage());
            }
        }
    }

}
