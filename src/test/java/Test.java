import org.apache.commons.io.FileUtils;

import java.io.File;

public class Test {
    public static void main(String[] args) throws Exception {
        File file = new File("d:/tmp.txt");
        FileUtils.write(file,"a");
        FileUtils.write(file,"b");
    }
}
