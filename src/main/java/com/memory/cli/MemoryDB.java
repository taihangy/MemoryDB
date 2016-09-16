package com.memory.cli;

import com.memory.database.MemoryDatabase;

import java.util.Scanner;

/**
 * Created by yetaihang on 9/15/16.
 */
public class MemoryDB {
    private static MemoryDatabase<String, String> db;

    /**
     * -s key value; -f key; -r key
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
        System.out.println("DB created. \nUsage:\n1. set key value: set the key with value\n2. find key: find the value of the key\n" +
                "3. remove key: remove the value of this key\n4. quit: quit database\n5. print: show database");
        System.out.print("> ");
        for (String op = scan.next(); !op.equalsIgnoreCase("quit"); op = scan.next()) {
            if (op.equalsIgnoreCase("set")) {
                String key = scan.next();
                String value = scan.next();
                db.set(key, value);
                System.out.println("After your set, database: " + db);
            }
            else if (op.equalsIgnoreCase("find")) {
                String key = scan.next();
                System.out.println(db.get(key));
                System.out.println("After your find, database: " + db);
            }
            else if (op.equalsIgnoreCase("remove")) {
                String key = scan.next();
                db.remove(key);
                System.out.println("After your remove, database: " + db);
            }
            else if (op.equalsIgnoreCase("print")) {
                System.out.println("database: " + db);
            }
            else {
                System.out.println("Usage:\n1. set key value: set the key with value\n2. find key: find the value of the key\n" +
                        "3. remove key: remove the value of this key\n4. quit: quit database\n5. print: show database");
            }
            System.out.print("> ");
        }
        System.exit(0);

    }
}
