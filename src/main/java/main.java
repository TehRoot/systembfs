import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.io.Files;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class main {
    static main obj = new main();
    public static void main(String[] args){
        int test = 30000005;
        ArrayList<Integer> system_list;
        ListMultimap<Integer, Integer> listMap;
        String file = obj.zipDecompress();
        system_list = obj.returnSystemList(file);
        listMap = obj.getSystemList(system_list, file);
        Integer[] integers = listMap.keySet().toArray(new Integer[listMap.size()]);
        ArrayList<Integer> systemList = new ArrayList<>();
        //ADD ALL NON-WORMHOLE SYSTEMS TO LIST
        for(int i=0;i<integers.length;i++){
            if(integers[i] != null){
                systemList.add(integers[i]);
            }
        }
        //SORT SYSTEMLIST
        Collections.sort(systemList);

        bfs_test(file, listMap, test, systemList);

    }

    public static void bfs_a(String file, ListMultimap<Integer, Integer> listMap, int startNode, ArrayList<Integer> conversionList){
        HashMap<Integer, Boolean> visitedMap = new HashMap<>();

        for(int key : conversionList){
            visitedMap.put(key, false);
        }

        LinkedList<Integer> queue = new LinkedList<>();
        visitedMap.replace(startNode, true);
        queue.add(startNode);

        while(!queue.isEmpty()){

        }



    }

    public static void bfs_test(String file, ListMultimap<Integer, Integer> listMap, int startNode, ArrayList<Integer> conversionList){
        //create hashmap to store system ID and whether the system has been visited or not, to avoid conversion to array type
        HashMap<Integer, Boolean> visitedMap = new HashMap<>();

        //take the system in the conversion list and add it to the hashmap and set it to false it's visited
        for(int key : conversionList){
            visitedMap.put(key, false);
        }
        //create a new queue
        LinkedList<Integer> queue = new LinkedList<>();
        //mark the first node passed visited in as true in the map
        visitedMap.replace(startNode, true);
        //add the node to the queue
        queue.add(startNode);
        //while the queue is not empty
        Iterator<Integer> i;
        while(queue.size() != 0){
            int n;
            startNode = queue.poll();
            //System.out.println(obj.returnSystemName(file, startNode)+" is connected to ");
            //create an iterator with the listMap that starts with the first node passed in.
            i = listMap.get(startNode).listIterator();
            //while the iterator has the next system or node
            while(i.hasNext())
            {
                //set n to the next system node/id
                n = i.next();
                System.out.println(obj.returnSystemName(file, n)+" is connected to ");
                //if the map has the system id/node, get the boolean value.
                if(!visitedMap.get(n))
                    visitedMap.replace(n, true);
                    queue.add(n);
                }
            }
        }

    public static void BFS(ListMultimap<Integer, Integer> listMap, int startNode, String file){
        Queue<Integer> queue = new ArrayDeque<>();
        HashSet<Integer> visited = new HashSet<>();
        queue.add(startNode);
        while(0 != queue.size()){
            int vertex = queue.poll();
            if(!visited.contains(vertex)){
                String system_name = obj.returnSystemName(file, vertex);
                System.out.print(system_name + " is connected to -> " );
                queue.addAll(listMap.get(vertex));
                visited.add(vertex);
            }
        }
    }

    public String returnSystemName(String file, int system_id) {
        String sql_Statement_3 = "SELECT solarSystemName FROM mapSolarSystems WHERE solarSystemID = ?";
        Connection connection;
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + file);
            PreparedStatement preparedStatement = connection.prepareStatement(sql_Statement_3);
            preparedStatement.setLong(1, system_id);
            ResultSet rs = preparedStatement.executeQuery();
            while(rs.next()){
                String system = rs.getString("solarSystemName");
                return system;
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<Integer> returnSystemList(String file) {
        String sql_Statement = "SELECT solarSystemID FROM mapSolarSystems ORDER BY solarSystemID ASC";
        ArrayList<Integer> system_list = new ArrayList<Integer>();
        Connection connection;
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + file);
            PreparedStatement preparedStatement = connection.prepareStatement(sql_Statement);
            ResultSet rs = preparedStatement.executeQuery();
            while(rs.next()){
                system_list.add(rs.getInt("solarSystemID"));
            }
        } catch ( SQLException e ) {
            e.printStackTrace();
        }
        return system_list;
    }

    public ListMultimap<Integer, Integer> getSystemList(ArrayList<Integer> system_list ,String file) {
        int system_id;
        String sql_Statement_2 = "SELECT toSolarSystemID FROM mapSolarSystemJumps WHERE fromSolarSystemID = ?";
        ListMultimap<Integer, Integer> multimap = ArrayListMultimap.create();
        for(int i=0;i<system_list.size();i++){
            system_id = system_list.get(i);
            Connection connection;
            try {
                connection = DriverManager.getConnection("jdbc:sqlite:" + file);
                PreparedStatement preparedStatement = connection.prepareStatement(sql_Statement_2);
                preparedStatement.setInt(1, system_id);
                ResultSet rs = preparedStatement.executeQuery();
                while(rs.next()){
                    multimap.put(system_id, rs.getInt("toSolarSystemID"));
                    //System.out.println("System: " + system_id + "" + multimap.get(system_id));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return multimap;
    }

    public String zipDecompress(){
        File folder = Files.createTempDir();
        byte[] buffer = new byte[1024];
        //System.out.println(folder);
        InputStream in = getClass().getResourceAsStream("/staticdataexport.zip");
        try {
            ZipInputStream zis = new ZipInputStream(in);
            ZipEntry ze = zis.getNextEntry();
            while(ze != null) {
                String fileName = ze.getName();
                File newFile = new File(folder + File.separator + fileName);
                System.out.println("Unzipped to: " +newFile);
                new File(newFile.getParent()).mkdirs();
                FileOutputStream fos = new FileOutputStream(newFile);
                int len;
                while((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
                return newFile.toString();
            }
            zis.closeEntry();
            zis.close();
            System.out.println("Unzipped");
        } catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }
}
