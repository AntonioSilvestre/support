package it.agevoluzione.utils.reminder;

import org.junit.Test;

public class BitWiseTest {

    @Test
    public void ciao() {
        int a = 1;
        int b = 2;

        int and = a & b;
        int or = a | b;
        System.out.println("bitwise and = " + and);
        System.out.println("bitwise or = " + or);
    }

    @Test
    public void test1() {
        testSelector(ALL);
        testSelector(SECONDA);
        testSelector(PRIMA|SECONDA);
        testSelector(SECONDA|QUARTA);
        testSelector(PRIMA|TERZA|SECONDA);
        testSelector(0);
        testSelector(QUARTA|PRIMA|TERZA|SECONDA);
    }

    final static int PRIMA = 1;     // 0001
    final static int SECONDA = 2;   // 0010
    final static int TERZA = 4;     // 0100
    final static int QUARTA = 8;    // 1000
    final static int ALL = 15;      // 1111
    final static int PRIMA_MASK = 1;
    final static int SECONDA_MASK = 3;
    final static int TERZA_MASK = 7;
    final static int QUARTA_MASK = 15;
    final static int MASK = 1;

    public void testSelector(int flag) {
//        System.out.println("In Ingresso");
//        print(flag);

//        System.out.println("condizioni...");
//        print(flag & PRIMA);
//        print(flag & SECONDA);
//        print(flag & TERZA);
//        print(flag & QUARTA);
//        print(QUARTA & flag);

//        System.out.println("Test");

        StringBuilder sb = new StringBuilder();
        boolean nonUsed = true;


        if ((flag & ALL) == ALL) {
            if (!nonUsed) {
                sb.append(" | ");
            }
            sb.append("ALL");
            nonUsed = false;
        } else {
            if ((flag & PRIMA) == PRIMA) {
                sb.append("PRIMA");
                nonUsed = false;
            }

            if ((flag & SECONDA) == SECONDA) {
                if (!nonUsed) {
                    sb.append(" | ");
                }
                sb.append("SECONDA");
                nonUsed = false;
            }
            if ((flag & TERZA) == TERZA) {
                if (!nonUsed) {
                    sb.append(" | ");
                }
                sb.append("TERZA");
                nonUsed = false;
            }
            if ((flag & QUARTA) == QUARTA) {
                if (!nonUsed) {
                    sb.append(" | ");
                }
                sb.append("QUARTA");
                nonUsed = false;
            }
        }

        if (nonUsed){
            sb.append("NULLA");
        }

        System.out.println(sb.toString()+"\n----------------");
    }

    public void print(int value){
        System.out.println("decimal: "+value);
        System.out.println("binary: "+Integer.toBinaryString(value));
    }
}
