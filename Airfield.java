import java.util.HashMap;
import java.util.Locale;

public class Airfield {
    public String name;
    public HashMap<Long,Integer> map;

    public Airfield(String name) {
        this.name = name;
        this.map = new HashMap<>();
    }

    
}
