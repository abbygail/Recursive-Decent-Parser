import java.io.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;

class Window extends JFrame{    
    //Constructor takes as input dimensions, title, and layout type
    
    ButtonGroup bg;
    public Window(int m, int n, String Name, LayoutManager lay){
        super(Name);        
        setSize(m,n);        
        setLayout(lay);        //lay determined from layout_type method
        bg = new ButtonGroup(); //initializes buttongroup
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    public void display(){setVisible(true);}    
}

class Panel extends JPanel{    
    //Constructor takes layout type as input
    ButtonGroup bg;
    public Panel(LayoutManager lay){
        setLayout(lay);
        bg = new ButtonGroup();
    }
}

class Parser {
    Window win;
    Token t;
    
    //Parser takes head of token list as input
    public Parser(Token T){        
        t=T;
        gui();
    }    
    
   private void gui(){
       
        String windowName;
        int winLength;
        int winWidth;               
        String end = "End.";

        //check to make sure file starts with "Window"
        if(!(t.tokenValue.equalsIgnoreCase("Window")))
        { 
            System.out.print("Gui Creation must start with 'Window'\n");
            return;
        }
        
        t=t.tail; //if file starts with window, check next token
        
        //Next token is name of window. Separate out quotes and only use string
        String temp[]=t.tokenValue.split("\"", 3);
        windowName=temp[1];         
        
        t=t.tail; // move on to next token in list - should be window dimensions (NUMBER,
        
        //checks for parenthesis and comma in dimensions
        if(t.tokenValue.charAt(0) != '(' || t.tokenValue.charAt(t.tokenValue.length()-1) != ',') //if the next character isn't a parenthesis or last isn't a comma
        {
            System.out.print("Define window dimensions in the form (Number, Number)\n");
            return;
        }
        
        //if dimensions are in proper format, split out parenthesis and comma to get integer
        String findNum[] = (t.tokenValue).split("", 2);
        findNum = findNum[1].split(",", 2);
        
        //checks for integer value
        try{
        winLength = Integer.parseInt(findNum[0]); }
        catch(NumberFormatException e){
             System.out.print("Dimensions must be integers\n");
            return;
        }
        
        t=t.tail; // moves on to second value in dimensions, int the form NUMBER)
        
        //checks for ending parenthesis
        if(t.tokenValue.charAt(t.tokenValue.length()-1) != ')')
        {
            System.out.print("Define window dimensions in the form (Number, Number)\n");
            System.exit(0);
        }        
        
        //splits number out from parenthesis
        findNum = (t.tokenValue).split("\\)", 2);
        
        //checks for integer value in dimension
        try{
            winWidth = Integer.parseInt(findNum[0]); }
        catch(NumberFormatException e){
             System.out.print("Dimensions must be integers\n");
            return;
        }
        
        //construcs gui window with dimentions, name, and calls layout method
        win = new Window(winLength, winWidth, windowName, layout());
       
        
        //calls widgets method to collect components for GUI
        JComponent[] jcomps = widgets();       
        
        //once components has been find, while loop puts components on GUI
        int i =0;
        while(jcomps[i] != null){
            
            win.add(jcomps[i]);            
            i++;
        }
        
        //if the last token in the file isn't "End.", throws an error
        if(!(t.tokenValue.equalsIgnoreCase(end)))
        {
            System.out.print("File must end with 'End.'");
            System.exit(0);
        }   
        
        win.display();
    }   
    
    private LayoutManager layout(){
        
        t = t.tail; //moves to token "Layout"
        
        //checks for keyword "Layout"
        if(!(t.tokenValue.equalsIgnoreCase("Layout")))
        {
            System.out.print("Must declare 'Layout' before layout type");
            System.exit(0);
            return null;
        }
        else
        {return layout_type();}    //if "Layout" is found, searches for layout type    
    }
    
    public LayoutManager layout_type(){
        t=t.tail; //moves to layout type
        
        //checks for flow or grid types, returns layout type
        if(t.tokenValue.equals("Flow:"))        
            {
                //new
                t=t.tail;
                return new FlowLayout();
            }        
        else if(t.tokenValue.contains("Grid"))
        {   
            //parses through number tokens to find dimensions for grid
            //separates out numbers from commas, then uses a 
            //try-catch block to check for int values
            int grid[] = new int[4];
            String temp[]= t.tokenValue.split("\\(",2);
            
            temp=temp[1].split(",", 2);
            try{
            grid[0]=Integer.parseInt(temp[0]); //first required integer
            t=t.tail;   //move to second integer
            
            //if this is not the last number (as signified by a closing parenthesis)
            //then check for other numbers
            //otherwise, skip straight to parsing last number
            String end= ")";
            if(!(t.tokenValue.contains(end)))
            {
                System.out.print("four numbers "+t.tokenValue+"\n");
                temp =t.tokenValue.split(",",2);            
                grid[1]=Integer.parseInt(temp[0]);
                t=t.tail; 

                temp= t.tokenValue.split(",",2);
                grid[2]=Integer.parseInt(temp[0]);
                t=t.tail; 
            }
            
            //checks for ending of :            
            if(t.tokenValue.charAt(t.tokenValue.length()-1) != ':'){
                System.out.print("Missing : after Grid dimensions\n" + t.tokenValue.charAt(t.tokenValue.length()-1));
                System.exit(0);
            }
            
            temp = t.tokenValue.split("\\)",2);
            grid[3]=Integer.parseInt(temp[0]);
            }
            catch(NumberFormatException e){
                System.out.print("Grid layout must be in format Grid(Number, Number): of Grid(Number, Number, Number, Number):");
                System.exit(0);
                return null;
            }
          //new
            t=t.tail;
            if(grid[1] == 0)
            {return new GridLayout(grid[0], grid[3]);}
            else
            {return new GridLayout(grid[0], grid[1], grid[2], grid[3]);}
        }
        else{       //if token isn't grid or flow, throws error 
            System.out.print("Layout type must be 'Flow:' or Grid(Number, Number, Number, Number):\n");
            System.exit(0);
            return null;
        }
    }
    
    private JComponent[] widgets(){     //widgets method returns Component array
       
       //initializes an array for components
        JComponent[] comps = new JComponent[20];
               
        // first item in array is the current widget
        comps[0] = widget();
        
        //if next token is a widget, recursion, otherwise returns null
        if(t.tokenValue.equalsIgnoreCase("Button")||t.tokenValue.equalsIgnoreCase("Panel")||t.tokenValue.equalsIgnoreCase("Label")||t.tokenValue.equalsIgnoreCase("Group")||t.tokenValue.equalsIgnoreCase("Textfield")){
            
            JComponent temp[] = widgets();                                            
            int i =1;                        
            while(temp[i-1] != null){                                  
                comps[i] = temp[i-1];
                i++;
            }      
        }
        else{
         comps[1] = null;   
        }
        return comps;
    }
    
    private JComponent widget(){       
        
        //gets component type as string, then uses if statements
        //to determine which component to use
        String widget = t.tokenValue;
        
        if(widget.equalsIgnoreCase("Button")){            
            t=t.tail;   //moves to button label
            
            //checks for ending ;
            if(t.tokenValue.charAt(t.tokenValue.length()-1) != ';')
            { 
                System.out.print("Missing ;");
                System.exit(0);
                return null;
            } 
            
            //splits out title from quotes. It no title exists, labels it blank
            String temp[] = t.tokenValue.split("\"", 3);
            if(temp[1].matches("[a-zA-Z0-9]+") == true){                               
                String buttonName = temp[1];               
                t=t.tail;               
                return new JButton(buttonName);
            }
            else{return new JButton(" ");}            
        }        
        else if(widget.equalsIgnoreCase("Group")){
            
            t=t.tail;
            //initializes array for radio buttons
            JRadioButton[] RButtons = radio_buttons();            
            Panel p = new Panel(new GridLayout(10,1));
            //adds each radio button to window, and adds button to group
            int i =0;            
            while(RButtons[i] != null){               
                p.add(RButtons[i]);
                p.bg.add(RButtons[i]);
                i++;
            }
                
            //checks for "End;" at the end of Group
            if(!(t.tokenValue.equalsIgnoreCase("End;"))){
                System.out.print("Group must end with 'End;'");
                System.exit(0);
            }            
            t=t.tail;              
            return p;            
        }
        else if(widget.equalsIgnoreCase("Label")){
            t=t.tail; //moves to label value
            String labelName=t.tokenValue;
            
            //checks for ending ;
            if(labelName.charAt(labelName.length()-1) != ';')
                {
                    System.out.print("Missing ;");
                    System.exit(0);
                    return null;
                } 
            
            //splits label from quotes. if empty, adds an empty label
            String temp[] = t.tokenValue.split("\"", 3);
            labelName = temp[1]; 
            if(labelName.matches("[a-zA-Z]+") == true)
                {
                    t=t.tail; 
                    return new JLabel(labelName);
                }            
            else{
                t=t.tail;
                return new JLabel(" ");
            }          
        }
        else if(widget.equalsIgnoreCase("Panel")){    
            //initializes panel by calling layout method
            Panel P = new Panel(layout());              
            
            //gets component array to add to panel later
            JComponent[] jcomps= widgets();
            
            //while loop adds the components to the panel
            int i =0;
            while(jcomps[i] != null)
            {
                P.add(jcomps[i]);
                i++;
            }
            
            //checks for "End;" at the end of the panel grammat
            if(!(t.tokenValue.equals("End;"))){
                System.out.print("Panel must end with 'End;'\n");
                System.exit(0);
                return null;
            } 
            
            t=t.tail;          
            return P;            //returns the panel as a component
        }
        else if(widget.equalsIgnoreCase("Textfield"))
        {
            t=t.tail;             
            
            //checks for ending ;
            if(t.tokenValue.charAt(t.tokenValue.length()-1) != ';')
                {
                    System.out.print("Textfield label missing ;\n");
                    System.exit(0);
                }
            
            //splits out ; from label value
            String temp[]=t.tokenValue.split(";", 2);
            int fieldSize=0;
            
            //checks for an integer
            try{
                fieldSize = Integer.parseInt(temp[0]);}
            catch(NumberFormatException e){
                System.out.print("Textfield size must be an integer\n");
                System.exit(0);
            }
            
            t=t.tail; 
            return new JTextField(fieldSize);
        }       
        else if(widget.contains("End")){   //checks for "End" keyword         
            t.flag = false;
            return null;           
        }
        else{
            System.out.print("Components must be Button, Textfield, Panel, Group, or Label\n");
            System.exit(0);
            return null;
        }
    }
    
    private JRadioButton[] radio_buttons(){     
        
        //intializes an array of radio buttons
        JRadioButton[] RButtons = new JRadioButton[20];
        
        //first item in array is the current radio button
        RButtons[0] = radio_button();
        t=t.tail;
        
        //if the next token is a radio button, recurstion, otherwise return null
        if(t.tokenValue.equals("Radio")){
            JRadioButton temp[] = radio_buttons();                                            
            int i =1;                        
            while(temp[i-1] != null){                                  
                RButtons[i] = temp[i-1];
                i++;
            }      
        }
        else{
         RButtons[1] = null;   
        }        
        return RButtons;
    }
    
    private JRadioButton radio_button(){
        
        //checks for "End" keyword
        if(t.tokenValue.equalsIgnoreCase("End;"))
        {
            t.flag = false;            
            return null;
        }
        else if(!(t.tokenValue.equalsIgnoreCase("Radio"))){     //checks for an error
            System.out.print("Group components must start with 'Radio' not "+t.tokenValue+"\n");
            System.exit(0);
        } 
        t=t.tail; // moves to radio button label
        
        //if no error found, splits title out from quotes
        String temp[] = t.tokenValue.split("\"", 3);       
        
        //checks for ending ;
        if(!(temp[2].equals(";")))
            {
                System.out.print("Radio button label missing ;\n");
                System.exit(0);
            }                
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
        
        //takes user input and attempts to open file
        String userFile;
        Scanner s = new Scanner(System.in);
        System.out.print("Enter file name:");
        userFile = s.nextLine();
        File file = new File(userFile);
        Scanner input=null;
        try {
            input = new Scanner(file);
        } catch (FileNotFoundException ex) {
            System.out.print("File not found\n");
            System.exit(0);            
        }

        //builds linked list of tokens
        Token first = new Token(input.next());
        Token curr = first;
       
        while(input.hasNext()){           
            curr.tail = new Token(input.next());
            curr = curr.tail;            
        }        
        input.close();        
        
        //starts parser
        Parser n = new Parser(first);
    }  
}