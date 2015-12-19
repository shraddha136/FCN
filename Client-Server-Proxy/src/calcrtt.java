/**
 *
 * @author Shraddha Atrawalkar
 */
public class calcrtt {

    private static long rtt_start = 0;
    private static long rtt_end = 0;

    public long getRtt_start() {
        return rtt_start;
    }

    public void setRtt_start(long rtt_start) {
        calcrtt.rtt_start = rtt_start;
    }

    public long getRtt_end() {
        return rtt_end;
    }

    public void setRtt_end(long rtt_end) {
        calcrtt.rtt_end = rtt_end;
    }

    public long calrtt() {
        return getRtt_end() - getRtt_start();
    }

}
