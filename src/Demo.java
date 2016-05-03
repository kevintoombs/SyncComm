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

    public class SendDemo implements Runnable {
        Channel c;

        public SendDemo(Channel in_c){
            this.c = in_c;
        }

        public void run() {
            String s = Thread.currentThread().toString() + " says hi!";
            c.Send(s);
        }
    }

    public class RecvDemo implements Runnable {
        Channel c;

        public RecvDemo(Channel in_c){
            this.c = in_c;
        }

        public void run() {
            Object o = c.Recv();
            if(o instanceof String)
            {
                String s = (String)o;
                System.out.println(s);
            }
        }
    }
}
