package com.queryToAST.app;

import com.queryToAST.app.Graph.Vertex.ClassEntity;
import java.util.List;
import java.util.Scanner;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws Exception
    {
        String internalName;
        Scanner in = new Scanner(System.in);
        System.out.print("Enter the path to the jar file:");
        
        internalName = in.nextLine();
        if(internalName.compareTo("") == 0)
        {
            internalName = "C:\\Users\\Niriel\\Documents\\NetBeansProjects\\JavaTestQueryToAST\\dist\\JavaTestQueryToAST.jar";
        }
        
        execute exec = new execute(internalName);
        
        while(true)
        {
            System.out.print("<: ");
            String query = in.nextLine();
            if(query.compareTo("") == 0)
            {
                break;
            }
            List<ClassEntity> result = exec.query(query);
            for(ClassEntity ce : result){
                System.out.println("FQN : " + ce.getFQN());
            }                     
        }
    }                    
}
