import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;



class menu
{
    private JFrame myWin;
    private MenuBar bar;
    private Menu fileMenu;
    private MenuItem exitItem;
    private MenuItem openFileItem;
    private FileDialog openFile;
    private Container myContainer;
    private JButton next;
    private boolean open_file_success = false;
    private int show_count = 0;




    public static byte[] input_data;
    public static boolean isII;
    public static int width = 0;
    public static int height = 0;
    public static List<Integer> BitsPerSample = new ArrayList<Integer>();
    public static int PixelBytes = 0;
    //public static int Compression = 0;
    public static int PhotometricInterpretation = 0;
    public static List<Integer> StripOffsets = new ArrayList<Integer>();
    //public static int RowsPerStrip = 0;
    public static List<Integer> StripByteCounts = new ArrayList<Integer>();
    //public static float XResolution = 0f;
    //public static float YResolution = 0f;
    //public static int ResolutionUnit = 0;
    //public static int Predictor = 0;
    public static List<Integer> SampleFormat = new ArrayList<Integer>();
    //public static String DateTime = "";
    //public static String Software = "";
    public static int[][] input;//gray-scale input
    public static int[][] dither_matrix;
    public static int dither_matrix_width;
    public static int dither_matrix_height;
    public static int[][] output_matrix;
    public static Color[] RGBvalue;
    public static Color[] Yvalue;
    public static Color[] dither_result;
    public static Color[] adjust_result;


    menu(){
        init();
    }


    static class Type_in_DE{
        public Type_in_DE(String name, int s){
            TagName = name;
            size = s;
        }
        public String TagName;
        public int size;
    }
    static private Type_in_DE[] TypeTable = {
            new Type_in_DE("???",0),        new Type_in_DE("byte",1),
            new Type_in_DE("ascii",1),      new Type_in_DE("short",2),
            new Type_in_DE("long",4),       new Type_in_DE("rational",8),
            new Type_in_DE("sbyte",1),      new Type_in_DE("undefined",1),
            new Type_in_DE("sshort",1),     new Type_in_DE("slong",1),
            new Type_in_DE("srational",1),  new Type_in_DE("float",4),
            new Type_in_DE("double",8)
    };


    public void init(){
        myWin = new JFrame("my window");
        myWin.setBounds(300,100,1080,720);
        myWin.setLocationRelativeTo(null);

        bar          = new MenuBar();
        fileMenu     = new Menu("Menu");
        openFileItem = new MenuItem("open file");
        exitItem     = new MenuItem("exit");

        fileMenu.add(openFileItem);
        fileMenu.add(exitItem);
        bar.add(fileMenu);

        myWin.setMenuBar(bar);

        openFile = new FileDialog(myWin,"Open",FileDialog.LOAD);



        myContainer   = myWin.getContentPane();
        myContainer.setBackground(Color.white);
        JPanel panel1 = new JPanel();
        JPanel panel2 = new JPanel();
        panel1.setBackground(Color.white);
        panel2.setBackground(Color.white);
        next = new JButton("Next");
        panel2.add(next);

        myContainer.add(panel2, BorderLayout.SOUTH);
        myContainer.add(panel1, BorderLayout.NORTH);
        myWin.setVisible(true);



        myEvent();
        myWin.setVisible(true);
    }

    private void myEvent(){
        openFileItem.addActionListener(e -> {
            openFile.setVisible(true);
            String dirPath = openFile.getDirectory();
            String fileName = openFile.getFile();
            String path = dirPath + fileName;
            System.out.println(path);



            if(dirPath == null || fileName == null){
                return;
            }
            else{
                open_file_success = true;
            }

            input_data = null;
            width = 0;
            height = 0;
            BitsPerSample = new ArrayList<Integer>();
            PixelBytes = 0;
            //Compression = 0;
            PhotometricInterpretation = 0;
            StripOffsets = new ArrayList<Integer>();
            //RowsPerStrip = 0;
            StripByteCounts = new ArrayList<Integer>();
            //XResolution = 0f;
            //YResolution = 0f;
            //ResolutionUnit = 0;
            //Predictor = 0;
            SampleFormat = new ArrayList<Integer>();
            //DateTime = "";
            //Software = "";
            input = null;//gray-scale input
            dither_matrix = null;
            dither_matrix_width = 0;
            dither_matrix_height = 0;
            output_matrix = null;
            RGBvalue = null;
            Yvalue = null;
            dither_result = null;
            adjust_result = null;





            try {
                InputStream stream = Files.newInputStream(Paths.get(path));
                input_data = stream.readAllBytes();



//                    byte[] temp= new byte[data.length];
//
//                    for(int i = 0; i < data.length; i++) {
//                        temp[i]=data[i];
//                    }

//                    for(int i = 0; i < temp.length; i++) {
//                        System.out.println(Integer.toHexString(temp[i] & 0xFF));
//                    }

//                    for(int i = 0; i < data.length; i++) {
//                        System.out.println(data[i]);
//                    }


                int pIFD = IFH_Decoder();
                System.out.println(pIFD);
                pIFD = IFD_Decoder(pIFD);


                System.out.println("ImageWidth"+width);
                System.out.println("ImageLength"+height);

                input           = new int[height][width];
                output_matrix   = new int[height][width];
                RGBvalue        = new Color[width*height];
                Yvalue          = new Color[width*height];
                adjust_result   = new Color[width*height];

                RGB_decoder();//load RGB to RGB_value
                //System.out.println(RGBvalue[0].getRed()+" "+RGBvalue[0].getGreen()+" "+RGBvalue[0].getBlue());
                //System.out.println(RGBvalue[RGBvalue.length-1].getRed()+" "+RGBvalue[RGBvalue.length-1].getGreen()+" "+RGBvalue[RGBvalue.length-1].getBlue());
                //System.out.println(RGBvalue[1].getRed()+" "+RGBvalue[1].getGreen()+" "+RGBvalue[1].getBlue());

                load_RGBvalue_to_Yvalue();//load Y to Y_value
                make_dither_matrix();
                ordered_dithering(input,height,width,output_matrix,dither_matrix,dither_matrix_height,dither_matrix_width);
                adjust_the_dynamic_range();

                BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
                int index = 0;
                for (int i=0; i<height; i++)
                {
                    for (int j=0; j<width; j++)
                    {
                        image.setRGB(j,i,RGBvalue[index++].getRGB());
                    }
                }
                ImageIcon icon = new ImageIcon(image);
//                    JOptionPane.showOptionDialog(null,"image",
//                            "Displays image",JOptionPane.DEFAULT_OPTION,JOptionPane.PLAIN_MESSAGE,
//                            icon,null,null);
//                    Image image_for_print = (Image) image;
//                    Graphics.drawImage(image_for_print, ImageWidth, ImageLength,null);


                show_count = 1;
                myContainer.remove(1);
                myContainer.add(new JLabel(icon));
                myWin.setVisible(true);

            } catch (IOException ex) {
                ex.printStackTrace();
            }

        });
        next.addActionListener(e -> {
            if(open_file_success){
                if(show_count == 0){
                    BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
                    int index=0;
                    show_count++;
                    for (int i=0; i<height; i++)
                    {
                        for (int j=0; j<width; j++)
                        {
                            image.setRGB(j,i,RGBvalue[index].getRGB());
                            index++;
                        }
                    }
                    ImageIcon icon = new ImageIcon(image);
                    myContainer.remove(1);
                    myContainer.add(new JLabel(icon));
                    myWin.setVisible(true);
                }
                else if(show_count == 1){
                    BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
                    int index=0;
                    show_count++;
                    for (int i=0; i<height; i++)
                    {
                        for (int j=0; j<width; j++)
                        {
                            image.setRGB(j,i,Yvalue[index].getRGB());
                            index++;
                        }
                    }
                    ImageIcon icon = new ImageIcon(image);
                    myContainer.remove(1);
                    myContainer.add(new JLabel(icon));
                    myWin.setVisible(true);
                }
                else if(show_count == 2){
                    BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
                    int index=0;
                    show_count++;
                    for (int i=0; i<height; i++)
                    {
                        for (int j=0; j<width; j++)
                        {
                            image.setRGB(j,i,dither_result[index].getRGB());
                            index++;
                        }
                    }
                    ImageIcon icon = new ImageIcon(image);
                    myContainer.remove(1);//Remove last image
                    myContainer.add(new JLabel(icon));
                    myWin.setVisible(true);
                }
                else{
                    BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
                    int index=0;
                    show_count=0;
                    for (int i=0; i<height; i++)
                    {
                        for (int j=0; j<width; j++)
                        {
                            image.setRGB(j,i,adjust_result[index].getRGB());
                            index++;
                        }
                    }
                    ImageIcon icon = new ImageIcon(image);
                    myContainer.remove(1);
                    myContainer.add(new JLabel(icon));
                    myWin.setVisible(true);
                }
            }
        });

        exitItem.addActionListener(e -> System.exit(0));

        myWin.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e){
                System.exit(0);
            }
        });
    }

    public static void main(String []args){
        new menu();
    }

    public int IFH_Decoder(){
        String byteOrder = Get_II_MM(0,2);
        if(byteOrder.equals("II")){
            isII = true;
        }
        else if(byteOrder.equals("MM")){
            isII = false;
        }
        else{
            System.out.println("The order value is not II or MM.");
        }


        int version = Get_Int_From_Input_Data(2, 2);

        if (version != 42){
            System.out.println("Not TIFF.");
        }

//        for(int i = 4; i < 8; i++) {
//             System.out.println(Integer.toHexString(data[i] & 0xFF));
//        }
        System.out.println("----\n");
        return Get_Int_From_Input_Data(4, 4);
    }

    public static String Get_II_MM(int startPosition, int Length){
        String temp = "";
        for (int i = 0; i < Length; i++){
            temp = temp + (char)input_data[startPosition + i];
        }
        return temp;
    }

    public static int Get_Int_From_Input_Data(int startPosition, int Length){
        int value = 0;
        if (isII){
            for (int i = 0; i < Length; i++){
                int temp = Byte.toUnsignedInt(input_data[startPosition + i]);
                //System.out.println(temp);
                temp = temp << (i*8);
                value = value | temp;
                //System.out.println(value);
            }
        }
        else {// MM
            for (int i = 0; i < Length; i++){
                int temp = Byte.toUnsignedInt(input_data[startPosition + Length - 1 - i]);
                //System.out.println(temp);
                temp = temp << (i*8);
                value = value | temp;
                //System.out.println(value);
            }
        }
        return value;
    }

    public static int IFD_Decoder(int Position){
        int n = Position;
        int DECount = Get_Int_From_Input_Data(n, 2);
        n += 2;
        for (int i = 0; i < DECount; i++)
        {
            DE_Decoder(n);
            n = n + 12;
        }

        return Get_Int_From_Input_Data(n, 4);//return next IFD address
    }

    public static void DE_Decoder(int Position){
        int Tag_Index = Get_Int_From_Input_Data(Position, 2);
        int Type_Index = Get_Int_From_Input_Data(Position + 2, 2);
        int Count = Get_Int_From_Input_Data(Position + 4, 4);


        int position_of_Data = Position + 8;
        int totalSize = TypeTable[Type_Index].size * Count;
        if (totalSize > 4){
            position_of_Data = Get_Int_From_Input_Data(position_of_Data, 4);
        }

        Get_DE_Value(Tag_Index, Type_Index, Count, position_of_Data);
    }

    public static void Get_DE_Value(int TagIndex, int TypeIndex, int Count, int position_of_Data){
        int size_of_type = TypeTable[TypeIndex].size;
        switch(TagIndex){
//            case 0x00fe: break;//New Sub file Type
//            case 0x00ff: break;//Sub file Type
            case 0x0100://ImageWidth
                width = Get_Int_From_Input_Data(position_of_Data,size_of_type);
                break;
            case 0x0101://ImageLength
                if (TypeIndex == 3){//Type 0003 Short
                    height = Get_Int_From_Input_Data(position_of_Data,size_of_type);
                    break;
                }
            case 0x0102://BitsPerSample
                for (int i = 0; i < Count; i++){
                    int v = Get_Int_From_Input_Data(position_of_Data+i*size_of_type,size_of_type);
                    BitsPerSample.add(v);
                    PixelBytes += v/8;
                }
                break;
//            case 0x0103: //Compression
//                Compression = GetInt(position_of_Data,size_of_type);break;
            case 0x0106: //Photometric Interpretation
                PhotometricInterpretation = Get_Int_From_Input_Data(position_of_Data,size_of_type);
                break;
            case 0x0111://StripOffsets
                for (int i = 0; i < Count; i++){
                    int v = Get_Int_From_Input_Data(position_of_Data+i*size_of_type,size_of_type);
                    StripOffsets.add(v);
                }
                break;
//            case 0x0112: break;//Orientation
//            case 0x0115: break;//Samples Per Pixel
//            case 0x0116://Rows Per Strip
//                RowsPerStrip = GetInt(position_of_Data,size_of_type);
//                break;
            case 0x0117://Strip Byte Counts
                for (int i = 0; i < Count; i++){
                    int v = Get_Int_From_Input_Data(position_of_Data+i*size_of_type,size_of_type);
                    StripByteCounts.add(v);
                }
                break;
//            case 0x011a: //X Resolution
//                XResolution = GetRational(position_of_Data); break;
//            case 0x011b://Y Resolution
//                YResolution = GetRational(position_of_Data); break;
//            case 0x011c: break;//Planar Config
//            case 0x0128://Resolution Unit
//                ResolutionUnit = GetInt(position_of_Data,size_of_type);break;
//            case 0x0131://software
//                Software = GetString(position_of_Data,size_of_type); break;
//            case 0x0132://dateTime
//                DateTime = GetString(position_of_Data,size_of_type); break;
//            case 0x013b: break;//artist
//            case 0x013d: //Differencing Predictor
//                Predictor = GetInt(position_of_Data,size_of_type);break;
//            case 0x0140: break;//Color Distribution Table
//            case 0x0152: break;//Extra Samples
            case 0x0153: //Sample Format
                for(int i = 0; i < Count; i++){
                    int v = Get_Int_From_Input_Data(position_of_Data+i*size_of_type,size_of_type);
                    SampleFormat.add(v);
                }
                break;

            default:
                break;
        }
    }

    public static void RGB_decoder(){
        int position = StripOffsets.get(0);
        //System.out.println(StripOffsets.size());
        for (int i = 0; i < height*width; i++)
        {
            int R = Byte.toUnsignedInt(input_data[position+3*i]);
            int G = Byte.toUnsignedInt(input_data[position+1+3*i]);
            int B = Byte.toUnsignedInt(input_data[position+2+3*i]);
            RGBvalue[i] = new Color(R,G,B);
        }
    }

    //    public static float GetRational(int startPosition){
//        int A = Get_Int_From_Input_Data(startPosition,4);
//        int B = Get_Int_From_Input_Data(startPosition+4,4);
//        return A / B;
//    }


    public static void load_RGBvalue_to_Yvalue(){
        int index = 0;//index of RGB value
        int Y = 0;
        for(int i=0;i<height;i++){
            for(int j=0;j<width;j++){
                Y = (int)(RGBvalue[index].getRed() * 0.299 + RGBvalue[index].getGreen() * 0.587 + RGBvalue[index].getBlue() * 0.114);
                input[i][j]=Y;
                Yvalue[index]=new Color(Y,Y,Y);
                index++;
            }
        }
    }

    public static void make_dither_matrix(){
        dither_matrix_width = 2;
        for(int i=8;i>=1;i--){
            if(width % i == 0){
                dither_matrix_width = i;
                break;
            }
        }
        System.out.println("dither_matrix_width: "+dither_matrix_width);
        dither_matrix_height = 2;
        for(int i=8;i>=1;i--){
            if(height % i == 0){
                dither_matrix_height = i;
                break;
            }
        }
        System.out.println("dither_matrix_width: "+dither_matrix_height);
        dither_matrix = new int[dither_matrix_height][dither_matrix_width];
        int []buffer = new int [dither_matrix_height*dither_matrix_width];
        for(int i=0;i<dither_matrix_height*dither_matrix_width;i+=2){
            buffer[i]=i;
            if(i+1<dither_matrix_height*dither_matrix_width){
                buffer[i+1]=dither_matrix_height*dither_matrix_width-i-1;
            }
        }
//        for(int i=0;i<dither_matrix_height*dither_matrix_width;i++){
//            buffer[i]=i;
//        }
        int index = 0;
        for(int i=0;i<dither_matrix_height;i++){
            for(int j=0;j<dither_matrix_width;j++){
                dither_matrix[i][j]=buffer[index++];
            }
        }
        printMatrix(dither_matrix,dither_matrix_height,dither_matrix_width);
    }

    public static void printMatrix(int[][] matrix,int rows, int columns){
        for(int i=0; i<rows; i++){
            for(int j=0; j<columns; j++){
                System.out.print(matrix[i][j]+"\t");
            }
            System.out.print("\n\n");
        }
    }

    public static void ordered_dithering(int[][] input, int input_rows, int input_columns, int[][] output, int[][] ditherMatrix, int ditherMatrix_rows, int ditherMatrix_columns){
        for(int i=0; i<input_rows; i+=ditherMatrix_rows){
            for(int j=0; j<input_columns; j+=ditherMatrix_columns){
                for(int k=0; k<ditherMatrix_rows;k++){
                    for(int l=0; l<ditherMatrix_columns;l++){
                        int value = input[i+k][j+l] / (256/(ditherMatrix_rows*ditherMatrix_columns+1));
                        if(value > ditherMatrix[k][l]){
                            output[i+k][j+l] = 1;
                        }
                        else{
                            output[i+k][j+l] = 0;
                        }
                    }
                }
            }
        }
        int index = 0;
        dither_result = new Color [input_columns * input_rows];
        for(int i=0; i<input_rows; i++){
            for(int j=0; j<input_columns; j++){
                if(output[i][j] == 1){
                    dither_result[index++] = new Color(255,255,255);
                }
                else{
                    dither_result[index++] = new Color(0,0,0);
                }
            }
        }
    }

    public static void adjust_the_dynamic_range(){
        for(int i=0; i<height*width;i++){
            int R = RGBvalue[i].getRed();
            int G = RGBvalue[i].getGreen();
            int B = RGBvalue[i].getBlue();
            get_better_RGB(R,G,B,i);
//            if(R<(256/4)){
//                R = (int)(R * 1.1);
//            }
//            else if(R>(256/4*3)){
//                R = (int)(R * 0.9);
//            }
//
//            if(G<(256/4)){
//                G = (int)(G * 1.1);
//            }
//            else if(G>(256/4*3)){
//                G = (int)(G * 0.9);
//            }
//
//            if(B<(256/4)){
//                B = (int)(B * 1.1);
//            }
//            else if(B>(256/4*3)){
//                B = (int)(B * 0.9);
//            }

//            adjust_result[i]=new Color(R,G,B);
        }
    }
    public static void get_better_RGB(int R, int G, int B, int index){
        int Y = (int)(0.299 * R + 0.587 * G + 0.114 * B);
        int U = (int)(-0.299 * R - 0.587 * G + 0.886 * B);
        int V = (int)(0.701 * R - 0.587 * G - 0.114 * B);
        if(Y < (256/8)){
            Y = (int)(Y * 1.4);
        }
        else if(Y > (256/8*6)){
            Y = (int)(Y * 0.95);
        }
        R = Y + V;
        G = (int)(Y - 0.194*U - 0.509*V);
        B = Y + U;

        if(G < 0){
            G = 0;
        }
        else if(G > 255){
            G = 255;
        }

        if(R < 0){
            R = 0;
        }
        else if(R > 255){
            R = 255;
        }

        if(B < 0){
            B = 0;
        }
        else if(B > 255){
            B = 255;
        }
        //System.out.println(G);
        adjust_result[index] = new Color(R,G,B);
    }
}
