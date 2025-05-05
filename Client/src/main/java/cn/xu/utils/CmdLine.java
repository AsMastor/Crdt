package cn.xu.utils;

import cn.xu.crdtObject.AwSet;
import cn.xu.crdtObject.LogList;
import cn.xu.crdtObject.LwwMap;
import cn.xu.crdtObject.MvMap;

import java.util.Scanner;

public class CmdLine {
    public static void run(AwSet awSet) {
        Scanner s = new Scanner(System.in);
        while (true) {
            String input = s.nextLine();
            String[] inputs = input.split(" ");
            if (inputs[0].equals("add")) {
                if (inputs.length == 2) {
                    awSet.add(inputs[1]);
                } else {
                    System.out.println("Illegal");
                }
            } else if (inputs[0].equals("remove") || inputs[0].equals("rmv")) {
                if (inputs.length == 2) {
                    awSet.remove(inputs[1]);
                } else {
                    System.out.println("Illegal");
                }
            } else if (inputs[0].equals("query") || inputs[0].equals("qry")) {
                if (inputs.length == 1) {
                    System.out.println(awSet.toString());
                } else {
                    System.out.println("Illegal");
                }
            } else {
                System.out.println("Illegal");
            }
        }
    }

    public static void run(MvMap mvMap) {
        Scanner s = new Scanner(System.in);
        while (true) {
            String input = s.nextLine();
            String[] inputs = input.split(" ");
            if (inputs[0].equals("add")) {
                if (inputs.length == 3) {
                    mvMap.add(inputs[1], inputs[2]);
                } else {
                    System.out.println("Illegal");
                }
            } else if (inputs[0].equals("remove") || inputs[0].equals("rmv")) {
                if (inputs.length == 2) {
                    mvMap.remove(inputs[1]);
                } else {
                    System.out.println("Illegal");
                }
            } else if (inputs[0].equals("query") || inputs[0].equals("qry")) {
                if (inputs.length == 1) {
                    System.out.println(mvMap.toString());
                } else {
                    System.out.println("Illegal");
                }
            } else {
                System.out.println("Illegal");
            }
        }
    }

    public static void run(LwwMap lwwMap) {
        Scanner s = new Scanner(System.in);
        while (true) {
            String input = s.nextLine();
            String[] inputs = input.split(" ");
            if (inputs[0].equals("add")) {
                if (inputs.length == 3) {
                    lwwMap.add(inputs[1], inputs[2]);
                } else {
                    System.out.println("Illegal");
                }
            } else if (inputs[0].equals("remove") || inputs[0].equals("rmv")) {
                if (inputs.length == 2) {
                    lwwMap.remove(inputs[1]);
                } else {
                    System.out.println("Illegal");
                }
            } else if (inputs[0].equals("query") || inputs[0].equals("qry")) {
                if (inputs.length == 1) {
                    System.out.println(lwwMap.toString());
                } else {
                    System.out.println("Illegal");
                }
            } else {
                System.out.println("Illegal");
            }
        }
    }

    public static void run(LogList logList) {
        Scanner s = new Scanner(System.in);
        while (true) {
            String input = s.nextLine();
            String[] inputs = input.split(" ");
            if (inputs[0].equals("add")) {
                if (inputs.length == 2) {
                    logList.add(inputs[1]);
                } else {
                    System.out.println("Illegal");
                }
            } else if (inputs[0].equals("query") || inputs[0].equals("qry")) {
                if (inputs.length == 1) {
                    System.out.println(logList.toString());
                } else {
                    System.out.println("Illegal");
                }
            } else {
                System.out.println("Illegal");
            }
        }
    }
}
