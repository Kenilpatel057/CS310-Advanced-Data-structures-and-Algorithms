// Best to test a class (here LineUsage) using a client like this in a separate class
public class TestLineUsage {
    public static void main(String[] args) {
        LineUsage lu = new LineUsage();
        lu.addObservation("OPERATOR");
        lu.addObservation("USERMGR");
        lu.addObservation("OPERATOR");
        Usage mc = lu.findMaxUsage();
        System.out.printf("%s - %d\n",mc.getUser(), mc.getCount());
    }
}
