public class Demo {

    public void Demo1() throws InterruptedException {
        Channel hi = new Channel();
        Thread t1 = new Thread(new SendDemo(hi));
        Thread t3 = new Thread(new SendDemo(hi));
        Thread t2 = new Thread(new RecvDemo(hi));
        Thread t4 = new Thread(new RecvDemo(hi));

        t1.start();
        t3.start();
        t2.start();
        t4.start();
        t1.join();
        t3.join();
        t2.join();
        t4.join();
    }

    public void Demo2() throws InterruptedException {
        int size = 100;
        Channel channel = new Channel();
        java.util.ArrayList<Thread> threads = new java.util.ArrayList<>();

        for (int i = 0; i < size; i++){
            if(i % 2 == 0){
                threads.add(new Thread(new SendDemo(channel)));
            } else {
                threads.add(new Thread(new RecvDemo(channel)));
            }
        }

        for (int i = 0; i < size; i++) {
            threads.get(i).start();
        }

        for (int i = 0; i < size; i++) {
            threads.get(i).join();
        }
    }

    public void Demo3() throws InterruptedException {
        int size = 100;
        Channel channel = new Channel();
        java.util.ArrayList<Thread> threads = new java.util.ArrayList<>();

        for (int i = 0; i < size; i++) {
            if (i < size / 2) {
                threads.add(new Thread(new SendDemo(channel)));
            } else {
                threads.add(new Thread(new RecvDemo(channel)));
            }
        }

        for (int i = 0; i < size; i++) {
            threads.get(i).start();
        }

        for (int i = 0; i < size; i++) {
            threads.get(i).join();
        }
    }

    public void Demo4(int size) throws InterruptedException {
        Channel channel = new Channel();
        java.util.ArrayList<Thread> threads = new java.util.ArrayList<>();

        for (int i = 0; i < size; i++) {
            threads.add(new Thread(new SendDemo(channel)));
            threads.get(i).start();
        }

        threads.add(new Thread(new RecvDemo(channel, size)));
        threads.get(size).start();

        for (int i = 0; i < size + 1; i++) {
            threads.get(i).join();
        }
    }

    public void Demo5(int size) throws InterruptedException {
        Channel channel = new Channel();
        java.util.ArrayList<Thread> threads = new java.util.ArrayList<>();

        for (int i = 0; i < size; i++) {
            threads.add(new Thread(new SendDemoStage2(channel)));
            threads.get(i).start();
        }

        threads.add(new Thread(new RecvDemoStage2(channel, size)));
        threads.get(size).start();

        for (int i = 0; i < size + 1; i++) {
            threads.get(i).join();
        }
    }

    public class SendDemo implements Runnable {
        Channel c;
        int messages = 1;

        public SendDemo(Channel in_c){
            this.c = in_c;
        }

        public SendDemo(Channel in_c, int in_messages) {
            this.c = in_c;
            this.messages = in_messages;
        }

        public void run() {
            String s = Thread.currentThread().toString() + " says hi!";
            for (int i = 0; i < messages; i++) {
                c.Send(s);
            }
        }
    }

    public class RecvDemo implements Runnable {
        Channel c;
        int messages = 1;

        public RecvDemo(Channel in_c){
            this.c = in_c;
        }

        public RecvDemo(Channel in_c, int in_messages) {
            this.c = in_c;
            this.messages = in_messages;
        }

        public void run() {
            for (int i = 0; i < messages; i++) {
                Object o = c.Recv();
                if (o instanceof String) {
                    String s = (String) o;
                    System.out.println(s);
                }
            }
        }
    }

    public class SendDemoStage2 implements Runnable {
        Channel c;
        int messages = 1;

        public SendDemoStage2(Channel in_c) {
            this.c = in_c;
        }

        public SendDemoStage2(Channel in_c, int in_messages) {
            this.c = in_c;
            this.messages = in_messages;
        }

        public void run() {
            String s = Thread.currentThread().toString() + " says hi!";
            for (int i = 0; i < messages; i++) {
                //c.Send(s);
                Object v = new SendEvent(s, c).sync();
            }
        }
    }

    public class RecvDemoStage2 implements Runnable {
        Channel c;
        int messages = 1;

        public RecvDemoStage2(Channel in_c) {
            this.c = in_c;
        }

        public RecvDemoStage2(Channel in_c, int in_messages) {
            this.c = in_c;
            this.messages = in_messages;
        }

        public void run() {
            for (int i = 0; i < messages; i++) {
                Object o = new RecvEvent(c).sync();
                if (o instanceof String) {
                    String s = (String) o;
                    System.out.println(s);
                }
            }
        }
    }
}
