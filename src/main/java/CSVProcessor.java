import java.io.*;
import java.nio.file.*;
import java.text.*;
import java.util.*;

/**
 * This Java application processes CSV file named "Test_task_1.csv"
 * Application is creating a separate .csv file for every unique ID,
 * stores all the data provided for the unique id in one file (multiple lines of one unique id if duplicate),
 * all the commas are replaced by dots in the field[2] (Total column)  and field[3] (Paid column),
 * it formats date in the field[4] (date column) and field[5] (no column)
 */
public class CSVProcessor {
    public static void main(String[] args) throws IOException, ParseException {
        // File path
        String filePath = "C:/Users/Donardas/Desktop/Test_task_1.csv";

        // Read file into a list of strings
        List<String> lines = Files.readAllLines(Paths.get(filePath));

        String header = lines.get(0);

        // Create a map to store unique ids and their data
        Map<String, List<String>> dataMap = new HashMap<>();

        for (int i = 1; i < lines.size(); i++) {
            String[] fields = lines.get(i).split(";");
            String id = fields[1];
            String date = fields[4];
            String no = fields[5];

            // Replace commas with dots
            fields[2] = fields[2].replace(",", ".");
            fields[3] = fields[3].replace(",", ".");
            // Format "date" column
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

            if (date.matches("\\d{2}/\\d{2}/\\d{4}")) {
                Date dateObj = sdf.parse(date);
                sdf.applyPattern("dd-MMM-yy");
                date = sdf.format(dateObj);
            }
            // Format "No" column
            if (no.matches("\\d{8}")) {
                // No need to format, as it's already in correct format
            } else if (no.matches("\\d{2}/\\d{2}/\\d{4}")) {
                sdf.applyPattern("dd/MM/yyyy");
                Date dateObj = sdf.parse(no);
                sdf.applyPattern("yyyy-MM-dd");
                no = sdf.format(dateObj);
            } else if (no.matches("\\d{4}.\\d{2}.\\d{2}")) {
                sdf.applyPattern("yyyy.MM.dd");
                Date dateObj = sdf.parse(no);
                sdf.applyPattern("yyyy-MM-dd");
                no = sdf.format(dateObj);
            }else if (no.matches("\\d{4}.\\d{4}")) {
                sdf.applyPattern("yyyy.MMdd");
                Date dateObj = sdf.parse(no);
                sdf.applyPattern("yyyy-MM-dd");
                no = sdf.format(dateObj);
            }
            else {
                System.out.println("Invalid date format for: " + date + " " + id);
            }

            // Update fields with the formatted values
            fields[4] = date;
            fields[5] = no;

            // Add data to the map
            if (!dataMap.containsKey(id)) {
                dataMap.put(id, new ArrayList<>());
            }
            dataMap.get(id).add(String.join(";", fields));
        }

        // Creating new files for each unique id
        for (Map.Entry<String, List<String>> entry : dataMap.entrySet()) {
            String id = entry.getKey();
            List<String> idData = entry.getValue();

            // Create a new file for this id
            String newFileName = id + ".csv";
            File newFile = new File(newFileName);
            newFile.createNewFile();

            // Write the data to the file
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(newFile))) {
                // Write the header
                bw.write(header);
                bw.newLine();
                for (String data : idData) {
                    bw.write(data);
                    bw.newLine();
                }
            }
        }
    }
}