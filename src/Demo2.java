public class Demo2 {
    public class SendDemo implements Runnable {
        Channel c;
        int messages = 1;

        public SendDemo(Channel in_c) {
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

        public RecvDemo(Channel in_c) {
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
}
