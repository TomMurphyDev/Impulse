package Azure;

/**
 * Created by Thomas Murphy on 20/03/2017.
 */
public class AppService {
    private static AppService ourInstance = new AppService();

    public static AppService getInstance() {
        return ourInstance;
    }

    private AppService() {
    }
}
