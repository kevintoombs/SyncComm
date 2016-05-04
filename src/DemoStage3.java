public class DemoStage3 {
    public class Sender implements Runnable {
        Channel c;
        int messages = 1;
        SelectionList list;

        public Sender(Channel in_c, int in_messages) {
            this.c = in_c;
            this.messages = in_messages;
        }

        public void run() {
            String s = Thread.currentThread().toString() + " says hi!";
            for (int i = 0; i < messages; i++) {
                Object v = new SendEvent(s, c).sync();
            }
        }
    }

    public class Recver implements Runnable {
        Channel c;
        int messages = 1;

        public Recver(Channel in_c) {
            this.c = in_c;
        }

        public Recver(Channel in_c, int in_messages) {
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
