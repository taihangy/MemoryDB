package com.memory.cli;

import com.memory.database.MemoryDatabase;

import java.util.Scanner;

/**
 * Created by yetaihang on 9/15/16.
 */
public class MemoryDB {
    private static MemoryDatabase<String, String> db;

    private static boolean isTransaction = false;

    /**
     * SET key value; FIND key; REMOVE key
     * @param args
     */
    public static void main(String[] args) {

        Scanner scan = new Scanner(System.in);

        if (args.length == 0 || args[0].equalsIgnoreCase("-h") || args[0].equalsIgnoreCase("--help")) {
            System.out.println("Usage: ./memdb start to start database");
            System.exit(0);
        }

        String isStart = args[0];

        if (!isStart.equalsIgnoreCase("start")) {
            System.out.println("Please type ./memDB start to start memory DB");
            System.exit(1);
        }
        db = new MemoryDatabase<String, String>(20, 20, 30);
        System.out.println("DB created. ");
        printUsage();
        System.out.print("> ");
        String[] tokens = scan.nextLine().split(" ");
        for (String op = tokens[0]; !op.equalsIgnoreCase("quit"); tokens = scan.nextLine().split(" "), op = tokens[0]) {
            if (op.equalsIgnoreCase("set") && tokens.length == 3) {
                String key = tokens[1];
                String value = tokens[2];
                db.set(key, value);
            }
            else if (op.equalsIgnoreCase("find") && tokens.length == 2) {
                String key = tokens[1];
                String value = db.get(key);
                if (!isTransaction)
                    System.out.println(value);
            }
            else if (op.equalsIgnoreCase("remove") && tokens.length == 2) {
                String key = tokens[1];
                db.remove(key);
            }
            else if (op.equalsIgnoreCase("print") && tokens.length == 1) {
                System.out.println("database: " + db);
            }
            else if (op.equalsIgnoreCase("multi") && tokens.length == 1) {
                isTransaction = true;
                db.multi();
            }
            else if (op.equalsIgnoreCase("exec") && tokens.length == 1) {
                isTransaction = false;
                db.exec();
            }
            else if (op.equalsIgnoreCase("rollback") && tokens.length == 1) {
                isTransaction = false;
                db.rollBack();
            }
            else {
                printUsage();
            }
            System.out.print("> ");
        }
        System.exit(0);

    }

    private static void printUsage() {
        System.out.println("Usage:\n1. set key value: set the key with value\n2. find key: find the value of the key\n" +
                "3. remove key: remove the value of this key\n4. multi: start transaction\n5. exec: commit transaction\n" +
                "6. rollback: not do all the command after multi" +
                "\n7. quit: quit database\n8. print: show database");
    }
}
