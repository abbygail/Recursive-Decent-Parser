import java.io.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.logging.Level;
import java.util.logging.Logger;

class Window extends JFrame{   
    public ButtonGroup bg;
    public Window(int m, int n, String Name, LayoutManager lay){
        
        super(Name);        
        setSize(m,n);        
        setLayout(lay);        
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    public void display(){setVisible(true);}    
}

class Panel extends JPanel{
    public ButtonGroup bg;
    public Panel(LayoutManager lay){
        setLayout(lay);
    }
}

class Parser {
    Window win;
    Token t;
    
    public Parser(Token T){        
        t=T;
        gui();
    }    
    
   private void gui(){
        String windowName;
        int winLength;
        int winWidth;        
        char close = ')';       
        String end = "End.";

        if(!(t.tokenValue.equalsIgnoreCase("Window"))){ //checks for keyword "Window"
          //throw exception and terminate
        }
        t=t.tail; //if we got window, check for string name
        String temp[]=t.tokenValue.split("\"", 3);
        windowName=temp[1]; //assign window name        
        
        t=t.tail;//Number 1
        
        if(t.tokenValue.charAt(0) != '(' || t.tokenValue.charAt(t.tokenValue.length()-1) != ',') //if the next character isn't a parenthesis or last isn't a comma
        {//throw an error            
        }
        String loseP[] = (t.tokenValue).split("", 2);
        String num[] = loseP[1].split(",", 2);
        winLength = Integer.parseInt(num[0]); 
        
        t=t.tail;//Number 2
        
        if(t.tokenValue.charAt(t.tokenValue.length()-1) != close)//checks for last parenthesis
        {//throws error            
        }        
        String loseP2[] = (t.tokenValue).split("\\)", 2);
        winWidth = Integer.parseInt(loseP2[0]);
        
        //LAYOUT CALL BELOW
        //t is number)
        win = new Window(winLength, winWidth, windowName, layout());
       
        int i =0;        
        JComponent[] jcomps = widgets(win);       
        while(jcomps[i] != null)
        {
            win.add(jcomps[i]);            
            i++;
        }
        
        if(!(t.tokenValue.equalsIgnoreCase(end)))
        {//throws error                    
        }   
        
        win.display();
    }   
    
    private LayoutManager layout(){
        t = t.tail;//layout
        if(!(t.tokenValue.equalsIgnoreCase("Layout")))
        {
            //error
            return null;
        }
        else
        {return layout_type();}        
    }
    
    public LayoutManager layout_type(){
        t=t.tail; //type
        if(t.tokenValue.contains("Flow"))        
            {return new FlowLayout();}        
        else if(t.tokenValue.contains("Grid"))
        {           
            //t is grid(number,
            int grid[] = new int[4];
            String temp[]= t.tokenValue.split("\\(",2);
            //temp[0] is grid, temp[1] is number,
            String temp2[]=temp[1].split(",", 2);
            //temp2[0] is number, temp2[1] is ,
            grid[0]=Integer.parseInt(temp2[0]);
            t=t.tail; //t is number2,
            
            String temp3[]= t.tokenValue.split(",",2);
            //temp3[0] is 3 temp3[1] is ????
            grid[1]=Integer.parseInt(temp3[0]);
            t=t.tail; //number 3
            
            String temp4[] = t.tokenValue.split(",",2);
            grid[2]=Integer.parseInt(temp4[0]);
            t=t.tail; //t is number4):
            
            String temp5[] = t.tokenValue.split("\\)",2);
            //temp5[0] is number temp5[1] is :
            grid[3]=Integer.parseInt(temp5[0]);
           
            return new GridLayout(grid[0], grid[1], grid[2], grid[3]);
        }
        else{return null;}//error
    }
    
    private JComponent[] widgets(Container c){
       t=t.tail; //textfield       
       
        JComponent[] comps = new JComponent[100];
        int i =0;        
        while(t.flag == true){             
            comps[i]=widget(c);
            i++;
        }
        return comps;
    }
    
    private JComponent widget(Container c){       
        String widget = t.tokenValue;
        if(widget.equalsIgnoreCase("Button")){            
            t=t.tail; //t is button number
            
            if(t.tokenValue.charAt(t.tokenValue.length()-1) != ';')
            { return null;} //error
            
            String temp[] = t.tokenValue.split("\"", 3);
            if(temp[1].matches("[a-zA-Z0-9]+") == true){                               
                String buttonName = temp[1];               
                t=t.tail;               
                return new JButton(buttonName);
            }
            else{return new JButton(" ");}            
        }        
        else if(widget.equalsIgnoreCase("Group")){
            JRadioButton[] RButtons = radio_buttons(c);
            
            int i =0;
            
            while(RButtons[i] != null){
                c.add(RButtons[i]);
                //RGroup.add(RButtons[i]);
                i++;
            }
                
            if(!(t.tokenValue.equalsIgnoreCase("End;"))){
                //throw an error
            }            
            t=t.tail;
            win.add(new JTextField(t.tokenValue));
           // return RGroup;            
            return null;
            //return new JRadioButtonMenuItem("cows");
        }
        else if(widget.equalsIgnoreCase("Label")){
            t=t.tail; //t is now label value
            String labelName=t.tokenValue;
            
             if(labelName.charAt(labelName.length()-1) != ';')
            {return null;} //error
            String temp[] = t.tokenValue.split("\"", 3);
            labelName = temp[1]; 
            if(labelName.matches("[a-zA-Z]+") == true)
                {
                    t=t.tail;
                    return new JLabel(labelName);
                }            
            else {
                t=t.tail;
                return new JLabel(" ");
            }
            //return null;            
        }
        else if(widget.equalsIgnoreCase("Panel")){                                              
            Panel P = new Panel(layout());  
            // t is still number): here
            int i =0;
            JComponent[] jcomps = widgets(P);           
            while(jcomps[i] != null)
            {
                P.add(jcomps[i]);
                i++;
            }
            t=t.tail;
            if(!(t.tokenValue.equals("End;"))){
                //error
            }
            t=t.tail;
            return P;            
        }
        else if(widget.equalsIgnoreCase("Textfield"))
        {
            t=t.tail; //Number
            String temp[]=t.tokenValue.split(";", 2);
            if(!(temp[1].equals(";")))
            {}//error
            
            int fieldSize = Integer.parseInt(temp[0]);            
            t=t.tail; //Panel
            return new JTextField(fieldSize);
        }       
        else{
            //error            
            t.flag = false;
            return null;           
        }
    }
    
    private JRadioButton[] radio_buttons(Container c){        
        JRadioButton[] RButtons = new JRadioButton[10];
        int i =0;
        while(t.flag ==true){
            t=t.tail;
            RButtons[i] = radio_button();
            //c.bg.add(RButton[i]);
            i++;
        }        
        return RButtons;
    }
    private JRadioButton radio_button(){
        if(!(t.tokenValue.equalsIgnoreCase("Radio"))){
            t.flag = false;
            return null;
        }        
        t=t.tail;
        String temp[] = t.tokenValue.split("\"", 3);        
        if(temp[2].equals(";"))
        {}                
        return new JRadioButton(temp[1]);
    }
}

class Token{        
    public String tokenValue;
    public boolean flag = true;
    public Token tail = null;
    LayoutManager lay = null;    
    
    public Token(String t){
        tokenValue = t;        
        tail = null;
    }
}

class Project1{
    public static void main(String[] args){       
        
        //opens file and scans
        String userFile;
        Scanner s = new Scanner(System.in);
        //System.out.print("Enter file name:");
        //userFile = s.nextLine();
        //File file = new File(userFile);
        File file = new File("potato.txt");
        Scanner input=null;
        try {
            input = new Scanner(file);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Project1.class.getName()).log(Level.SEVERE, null, ex);
        }
                
        Token first = new Token(input.next());
        Token curr = first;
       
        while(input.hasNext()){
           // String nextToken = input.next();
            curr.tail = new Token(input.next());
            curr = curr.tail;            
        }        
        input.close();        
        
        Parser n = new Parser(first);
    }  
}