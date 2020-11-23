package bgu.spl.mics;
//import com.google.gson.Gson;
import com.google.gson.Gson;
import com.google.gson.InstanceCreator;

import com.google.gson.annotations.SerializedName;

//import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class Agent
{
    public Agent(int id,char type)
    {
        this.id=id;
        this.type=type;
    }
   @SerializedName("agent_id") int id;
    char type;
}

class GSON_EXAMPLE_1
{
    public GSON_EXAMPLE_1(int[][] graph,Agent[] agents)
    {
        this.graph=graph;
        this.agents=agents;
    }
    int[][] graph;
    Agent[] agents;
}

public class GsonTest {


    private final String filepath="D:\\spl\\example.json";
    private Gson gson;
    @BeforeEach
    public void setUp(){
        gson = new Gson();
    }
    @Test
    public  void testoutputGson() throws IOException {

        int[][]graph={{1,2,3,4},{5,6,7,8},{9,10,11,12}};
        Agent[] agents={ new Agent(10,'c'),new Agent(-1,'v')};
        GSON_EXAMPLE_1 obj=new GSON_EXAMPLE_1(graph,agents);
        FileWriter fw=new FileWriter(filepath);
        gson.toJson(obj,fw);
        fw.close();
    }

    @Test
    public void testreadGson() throws FileNotFoundException {
        GSON_EXAMPLE_1 obj=gson.fromJson(new FileReader(filepath),GSON_EXAMPLE_1.class);
        Assertions.assertEquals(obj.graph.length,3);
        Assertions.assertArrayEquals(obj.graph[0],new int[]{1,2,3,4});
        Assertions.assertArrayEquals(obj.graph[1],new int[]{5,6,7,8});
        Assertions.assertArrayEquals(obj.graph[2],new int[]{9,10,11,12});
        Assertions.assertEquals(obj.agents[0].id,10);
        Assertions.assertEquals(obj.agents[1].id,-1);
        Assertions.assertEquals(obj.agents[0].type ,'c');
        Assertions.assertEquals(obj.agents[1].type,'v');

    }

}
