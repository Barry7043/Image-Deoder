import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;
//import static java.lang.Math.cos;
//import static java.lang.Math.sqrt;


class menu {
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
    public static Color[] huffman_RGBvalue;
    public static Map<Byte, String> huffmanCodes;
    public static StringBuilder stringBuilder;
    public static StringBuilder huffman_input_str;
    public static int size_before_compression;
    public static int size_after_huffman_compression;
    public static double ratio_huffman_compression;
//    public static double[][] matrix_T;
//    public static double[][] matrix_T_transpose;
//    public static int[][] DCT_Coefficient;
    public static int[][] my_compression_R;
    public static int[][] my_compression_G;
    public static int[][] my_compression_B;
    public static byte[] my_compression_output;
    public static Color[] output_Color_arr_temp;
    public static int size_after_my_compression;
    public static double ratio_my_compression_10;
    public static double ratio_my_compression_20;
    public static Color[] output_my_compression_10;
    public static Color[] output_my_compression_20;

//    public static List<String> inputArr = new ArrayList<String>();
//    public static List<String> lib = new ArrayList<String>();
//    public static List<Integer> output = new ArrayList<Integer>();
//    public static List<String> lib_of_decode = new ArrayList<String>();
//    public static List<String> decode_result = new ArrayList<String>();



    menu() {
        init();
    }

    static class Node implements Comparable<Node>{
        Byte data;
        Node left_node;
        Node right_node;
        int weight;

        public Node(Byte data, int weight) {
            this.data = data;
            this.weight = weight;
        }

        @Override
        public int compareTo(Node o) {
            return this.weight-o.weight;
        }

        @Override
        public String toString() {
            return "Node{" +
                    "data=" + data +
                    ", weight=" + weight +
                    '}';
        }
    }

    static class Type_in_DE {
        public Type_in_DE(String name, int s) {
            TagName = name;
            size = s;
        }

        public String TagName;
        public int size;
    }

    static private Type_in_DE[] TypeTable = {
            new Type_in_DE("???", 0), new Type_in_DE("byte", 1),
            new Type_in_DE("ascii", 1), new Type_in_DE("short", 2),
            new Type_in_DE("long", 4), new Type_in_DE("rational", 8),
            new Type_in_DE("sbyte", 1), new Type_in_DE("undefined", 1),
            new Type_in_DE("sshort", 1), new Type_in_DE("slong", 1),
            new Type_in_DE("srational", 1), new Type_in_DE("float", 4),
            new Type_in_DE("double", 8)
    };


    public void init() {
        myWin = new JFrame("my window");
        myWin.setBounds(300, 100, 1900, 900);
        myWin.setLocationRelativeTo(null);

        bar = new MenuBar();
        fileMenu = new Menu("Menu");
        openFileItem = new MenuItem("open file");
        exitItem = new MenuItem("exit");

        fileMenu.add(openFileItem);
        fileMenu.add(exitItem);
        bar.add(fileMenu);

        myWin.setMenuBar(bar);

        openFile = new FileDialog(myWin, "Open", FileDialog.LOAD);


        myContainer = myWin.getContentPane();
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

    private void myEvent() {
        openFileItem.addActionListener(e -> {
            openFile.setVisible(true);
            String dirPath = openFile.getDirectory();
            String fileName = openFile.getFile();
            String path = dirPath + fileName;
            System.out.println(path);


            if (dirPath == null || fileName == null) {
                return;
            } else {
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
            size_before_compression = 0;

//            inputArr = new ArrayList<String>();
//            lib = new ArrayList<String>();
//            lib.add("0");
//            lib.add("1");
//            output = new ArrayList<Integer>();
//            lib_of_decode = new ArrayList<String>();
//            decode_result = new ArrayList<String>();

            huffman_input_str = new StringBuilder();



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


                System.out.println("ImageWidth" + width);
                System.out.println("ImageLength" + height);

                input = new int[height][width];
                output_matrix = new int[height][width];
                RGBvalue = new Color[width * height];
                Yvalue = new Color[width * height];
                adjust_result = new Color[width * height];
                huffman_RGBvalue = new Color[width * height];
                huffmanCodes = new HashMap<>();
                stringBuilder = new StringBuilder();
//                matrix_T = new double[8][8];
//                matrix_T_transpose = new double[8][8];
//                DCT_Coefficient = new int[height][width];
                my_compression_R = new int[height][width];
                my_compression_G = new int[height][width];
                my_compression_B = new int[height][width];
                output_Color_arr_temp = new Color[width * height];
                output_my_compression_10 = new Color[width * height];
                output_my_compression_20 = new Color[width * height];

                RGB_decoder();//load RGB to RGB_value
                //System.out.println(RGBvalue[0].getRed()+" "+RGBvalue[0].getGreen()+" "+RGBvalue[0].getBlue());
                //System.out.println(RGBvalue[RGBvalue.length-1].getRed()+" "+RGBvalue[RGBvalue.length-1].getGreen()+" "+RGBvalue[RGBvalue.length-1].getBlue());
                //System.out.println(RGBvalue[1].getRed()+" "+RGBvalue[1].getGreen()+" "+RGBvalue[1].getBlue());

                load_RGBvalue_to_Yvalue();//load Y to Y_value
                make_dither_matrix();
                ordered_dithering(input, height, width, output_matrix, dither_matrix, dither_matrix_height, dither_matrix_width);
                adjust_the_dynamic_range();


                BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
                int index = 0;
                for (int i = 0; i < height; i++) {
                    for (int j = 0; j < width; j++) {
                        image.setRGB(j, i, RGBvalue[index++].getRGB());
                    }
                }
                ImageIcon icon = new ImageIcon(image);
//                    JOptionPane.showOptionDialog(null,"image",
//                            "Displays image",JOptionPane.DEFAULT_OPTION,JOptionPane.PLAIN_MESSAGE,
//                            icon,null,null);
//                    Image image_for_print = (Image) image;
//                    Graphics.drawImage(image_for_print, ImageWidth, ImageLength,null);


/////////////////////////  <-------  Lossless Compression  ------->  ////////////////////////////////////////////


//                RGB_to_string_inputArr(RGBvalue,width,height,inputArr);//for lzw compression

//                int rgb = 0;
//                char temp =255;
//                char temp2 =(char)rgb;
//                char temp3 = 120;
//                String str = Character.toString(temp);
//                System.out.println(str+"\n");
//                str += temp2;
//                str += temp3;
//                System.out.println(str+"\n");
//                int result = str.charAt(0);
//                System.out.println(result+"\n");

//                LZWcoding(inputArr,lib,output);
//                LZWdecode(output,decode_result);
//                System.out.println(inputArr.size()+" size---\n");
//                RGB_to_string_inputArr();
//                System.out.println(input_str);
//

                RGB_to_string_inputArr();
//                System.out.println(input_str);
                byte[] contentBytes = huffman_input_str.toString().getBytes();
                byte[] huffmanCodeBytes = huffman_compression(contentBytes);
                byte[] bytes = huffman_decode(huffmanCodes, huffmanCodeBytes);
                String huffman_decode_result_str = new String(bytes);
//                System.out.println(huffman_decode_result_str);
                int position = 0;
                for (int i = 0; i < height * width; i++) {
                    int R = huffman_decode_result_str.charAt(position++);
                    int G = huffman_decode_result_str.charAt(position++);
                    int B = huffman_decode_result_str.charAt(position++);
                    if(R<0){
                        R = 0;
                    }
                    else if(R>255){
                        R = 255;
                    }
                    if(G<0){
                        G = 0;
                    }
                    else if(G>255){
                        G = 255;
                    }
                    if(B<0){
                        B = 0;
                    }
                    else if(B>255){
                        B = 255;
                    }
                    huffman_RGBvalue[i] = new Color(R, G, B);
                }


/////////////////////////  <---------  Lossy Compression  --------->  ////////////////////////////////////////////


                my_own_Compression(10);
                List<Integer> my_compression_result1 = RLE_compression();
                byte[] output_of_ratio_10 = int_arr_to_byte_arr(my_compression_result1);
                output_my_compression_10 = my_decode(output_of_ratio_10); // decode

                size_before_compression = width * height * 3 * 8;// in bits
                size_after_my_compression = 8 * output_of_ratio_10.length;// in bits
                ratio_my_compression_10 = size_before_compression*1.0/size_after_my_compression;

//                System.out.println(size_before_compression);
//                System.out.println(output_of_ratio_10.length*8);

                output_Color_arr_temp = new Color[width * height];

                my_own_Compression(20);
                List<Integer> my_compression_result2 = RLE_compression();
                byte[] output_of_ratio_20 = int_arr_to_byte_arr(my_compression_result2);
                output_my_compression_20 = my_decode(output_of_ratio_20); // decode

                size_before_compression = width * height * 3 * 8;// in bits
                size_after_my_compression = 8 * output_of_ratio_20.length;// in bits
                ratio_my_compression_20 = size_before_compression*1.0/size_after_my_compression;

/////////////////////////  <-------  Save Compressed File  ------->  ////////////////////////////////////////////


                String output_file_name = "../compressed_files/" + fileName + "_lossless.txt";
                File lossless_txt = new File(output_file_name);
                if (!lossless_txt.exists()) {
                    lossless_txt.createNewFile();
                }
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(output_file_name));
                bos.write(huffmanCodeBytes);
                bos.flush();
                bos.close();

                output_file_name = "../compressed_files/" + fileName + "_lossy_ratio_10.txt";
                lossless_txt = new File(output_file_name);
                if (!lossless_txt.exists()) {
                    lossless_txt.createNewFile();
                }
                bos = new BufferedOutputStream(new FileOutputStream(output_file_name));
                bos.write(output_of_ratio_10);
                bos.flush();
                bos.close();

                output_file_name = "../compressed_files/" + fileName + "_lossy_ratio_20.txt";
                lossless_txt = new File(output_file_name);
                if (!lossless_txt.exists()) {
                    lossless_txt.createNewFile();
                }
                bos = new BufferedOutputStream(new FileOutputStream(output_file_name));
                bos.write(output_of_ratio_20);
                bos.flush();
                bos.close();


/////////////////////////  <------------  Print Image  ------------>  ////////////////////////////////////////////


                BufferedImage decompressed_image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
                int index2 = 0;
                for (int i = 0; i < height; i++) {
                    for (int j = 0; j < width; j++) {
                        decompressed_image.setRGB(j, i, huffman_RGBvalue[index2++].getRGB());
                    }
                }
                ImageIcon icon2 = new ImageIcon(decompressed_image);


                size_before_compression = width * height * 3 * 8;// in bits
                size_after_huffman_compression = 8 * huffmanCodeBytes.length;// in bits
                ratio_huffman_compression = size_before_compression*1.0/size_after_huffman_compression;
//                System.out.println(size_before_compression);
//                System.out.println(size_after_huffman_compression);
//                System.out.println(ratio_huffman_compression);
                DecimalFormat df = new DecimalFormat("0.000");
//                System.out.println(df.format(ratio_huffman_compression));


                show_count = 1;
                myContainer.remove(1);
//                myContainer.add(new JLabel(icon));


                JPanel panel = new JPanel();
                JLabel label1 = new JLabel(icon2);
                Dimension size1 = label1.getPreferredSize();
                label1.setBounds(1900 / 2 - width - 50, (900 - height) / 2 - 100, size1.width, size1.height);
                JLabel label2 = new JLabel(icon);
                Dimension size2 = label2.getPreferredSize();
                label2.setBounds(1900 / 2 + 50, (900 - height) / 2 - 100, size2.width, size2.height);
                JLabel label3 = new JLabel("Ratio: " + df.format(ratio_huffman_compression));
                label3.setFont(new Font("Dialog", 1, 16));
                Dimension size3 = label3.getPreferredSize();
                label3.setBounds(1900 / 2 - width/2 - 50 - size3.width/2, (900 - height) / 2 + height - 85, size3.width, size3.height);
                panel.setLayout(null);
                panel.add(label1);
                panel.add(label2);
                panel.add(label3);
                panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                myContainer.add(panel);

                myWin.setVisible(true);

            } catch (IOException ex) {
                ex.printStackTrace();
            }

        });
        next.addActionListener(e -> {
            if (open_file_success) {
                if (show_count == 0) { // Lossless Compression image
                    BufferedImage origin_image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
                    int index = 0;
                    show_count++;
                    for (int i = 0; i < height; i++) {
                        for (int j = 0; j < width; j++) {
                            origin_image.setRGB(j, i, RGBvalue[index].getRGB());
                            index++;
                        }
                    }
                    BufferedImage decompressed_image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
                    int index2 = 0;
                    for (int i = 0; i < height; i++) {
                        for (int j = 0; j < width; j++) {
                            decompressed_image.setRGB(j, i, huffman_RGBvalue[index2++].getRGB());
                        }
                    }

                    ImageIcon icon2 = new ImageIcon(decompressed_image);
                    ImageIcon icon = new ImageIcon(origin_image);
                    myContainer.remove(1);
                    JPanel panel = new JPanel();
                    JLabel label1 = new JLabel(icon2);
                    Dimension size1 = label1.getPreferredSize();
                    label1.setBounds(1900 / 2 - width - 50, (900 - height) / 2 - 100, size1.width, size1.height);
                    JLabel label2 = new JLabel(icon);
                    Dimension size2 = label2.getPreferredSize();
                    label2.setBounds(1900 / 2 + 50, (900 - height) / 2 - 100, size2.width, size2.height);
                    DecimalFormat df = new DecimalFormat("0.000");
                    JLabel label3 = new JLabel("Ratio: " + df.format(ratio_huffman_compression));
                    label3.setFont(new Font("Dialog", 1, 16));
                    Dimension size3 = label3.getPreferredSize();
                    label3.setBounds(1900 / 2 - width/2 - 50 - size3.width/2, (900 - height) / 2 + height - 85, size3.width, size3.height);
                    panel.setLayout(null);
                    panel.add(label1);
                    panel.add(label2);
                    panel.add(label3);
                    panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                    myContainer.add(panel);
                    myWin.setVisible(true);
                } else if (show_count == 1) { // Lossy Compression in Ratio 10
                    BufferedImage origin_image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
                    int index1 = 0;
                    show_count++;
                    for (int i = 0; i < height; i++) {
                        for (int j = 0; j < width; j++) {
                            origin_image.setRGB(j, i, RGBvalue[index1].getRGB());
                            index1++;
                        }
                    }
                    ImageIcon origin_icon = new ImageIcon(origin_image);

                    BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
                    int index2 = 0;
                    show_count++;
                    for (int i = 0; i < height; i++) {
                        for (int j = 0; j < width; j++) {
                            image.setRGB(j, i, output_my_compression_10[index2].getRGB());
                            index2++;
                        }
                    }

                    ImageIcon icon = new ImageIcon(image);
                    myContainer.remove(1);
                    JPanel panel = new JPanel();
                    JLabel label1 = new JLabel(icon);
                    Dimension size1 = label1.getPreferredSize();
                    label1.setBounds(1900 / 2 - width - 50, (900 - height) / 2 - 100, size1.width, size1.height);
                    JLabel label2 = new JLabel(origin_icon);
                    Dimension size2 = label2.getPreferredSize();
                    label2.setBounds(1900 / 2 + 50, (900 - height) / 2 - 100, size2.width, size2.height);
                    DecimalFormat df = new DecimalFormat("0.000");
                    JLabel label3 = new JLabel("Ratio: " + df.format(ratio_my_compression_10));
                    label3.setFont(new Font("Dialog", 1, 16));
                    Dimension size3 = label3.getPreferredSize();
                    label3.setBounds(1900 / 2 - width/2 - 50 - size3.width/2, (900 - height) / 2 + height - 85, size3.width, size3.height);
                    panel.setLayout(null);
                    panel.add(label1);
                    panel.add(label2);
                    panel.add(label3);
                    panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                    myContainer.add(panel);
                    myWin.setVisible(true);
                } else {
                    BufferedImage origin_image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
                    int index1 = 0;
                    show_count++;
                    for (int i = 0; i < height; i++) {
                        for (int j = 0; j < width; j++) {
                            origin_image.setRGB(j, i, RGBvalue[index1].getRGB());
                            index1++;
                        }
                    }
                    ImageIcon origin_icon = new ImageIcon(origin_image);

                    BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
                    int index2 = 0;
                    show_count = 0;
                    for (int i = 0; i < height; i++) {
                        for (int j = 0; j < width; j++) {
                            image.setRGB(j, i, output_my_compression_20[index2].getRGB());
                            index2++;
                        }
                    }
                    ImageIcon icon = new ImageIcon(image);
                    myContainer.remove(1);
                    JPanel panel = new JPanel();
                    JLabel label1 = new JLabel(icon);
                    Dimension size1 = label1.getPreferredSize();
                    label1.setBounds(1900 / 2 - width - 50, (900 - height) / 2 - 100, size1.width, size1.height);
                    JLabel label2 = new JLabel(origin_icon);
                    Dimension size2 = label2.getPreferredSize();
                    label2.setBounds(1900 / 2 + 50, (900 - height) / 2 - 100, size2.width, size2.height);
                    DecimalFormat df = new DecimalFormat("0.000");
                    JLabel label3 = new JLabel("Ratio: " + df.format(ratio_my_compression_20));
                    label3.setFont(new Font("Dialog", 1, 16));
                    Dimension size3 = label3.getPreferredSize();
                    label3.setBounds(1900 / 2 - width/2 - 50 - size3.width/2, (900 - height) / 2 + height - 85, size3.width, size3.height);
                    panel.setLayout(null);
                    panel.add(label1);
                    panel.add(label2);
                    panel.add(label3);
                    panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                    myContainer.add(panel);
                    myWin.setVisible(true);
                }
            }
        });

        exitItem.addActionListener(e -> System.exit(0));

        myWin.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
    }

    public static void main(String[] args) {
        new menu();
    }

    public int IFH_Decoder() {
        String byteOrder = Get_II_MM(0, 2);
        if (byteOrder.equals("II")) {
            isII = true;
        } else if (byteOrder.equals("MM")) {
            isII = false;
        } else {
            System.out.println("The order value is not II or MM.");
        }


        int version = Get_Int_From_Input_Data(2, 2);

        if (version != 42) {
            System.out.println("Not TIFF.");
        }

//        for(int i = 4; i < 8; i++) {
//             System.out.println(Integer.toHexString(data[i] & 0xFF));
//        }
        System.out.println("----\n");
        return Get_Int_From_Input_Data(4, 4);
    }

    public static String Get_II_MM(int startPosition, int Length) {
        String temp = "";
        for (int i = 0; i < Length; i++) {
            temp = temp + (char) input_data[startPosition + i];
        }
        return temp;
    }

    public static int Get_Int_From_Input_Data(int startPosition, int Length) {
        int value = 0;
        if (isII) {
            for (int i = 0; i < Length; i++) {
                int temp = Byte.toUnsignedInt(input_data[startPosition + i]);
                //System.out.println(temp);
                temp = temp << (i * 8);
                value = value | temp;
                //System.out.println(value);
            }
        } else {// MM
            for (int i = 0; i < Length; i++) {
                int temp = Byte.toUnsignedInt(input_data[startPosition + Length - 1 - i]);
                //System.out.println(temp);
                temp = temp << (i * 8);
                value = value | temp;
                //System.out.println(value);
            }
        }
        return value;
    }

    public static int IFD_Decoder(int Position) {
        int n = Position;
        int DECount = Get_Int_From_Input_Data(n, 2);
        n += 2;
        for (int i = 0; i < DECount; i++) {
            DE_Decoder(n);
            n = n + 12;
        }

        return Get_Int_From_Input_Data(n, 4);//return next IFD address
    }

    public static void DE_Decoder(int Position) {
        int Tag_Index = Get_Int_From_Input_Data(Position, 2);
        int Type_Index = Get_Int_From_Input_Data(Position + 2, 2);
        int Count = Get_Int_From_Input_Data(Position + 4, 4);


        int position_of_Data = Position + 8;
        int totalSize = TypeTable[Type_Index].size * Count;
        if (totalSize > 4) {
            position_of_Data = Get_Int_From_Input_Data(position_of_Data, 4);
        }

        Get_DE_Value(Tag_Index, Type_Index, Count, position_of_Data);
    }

    public static void Get_DE_Value(int TagIndex, int TypeIndex, int Count, int position_of_Data) {
        int size_of_type = TypeTable[TypeIndex].size;
        switch (TagIndex) {
//            case 0x00fe: break;//New Sub file Type
//            case 0x00ff: break;//Sub file Type
            case 0x0100://ImageWidth
                width = Get_Int_From_Input_Data(position_of_Data, size_of_type);
                break;
            case 0x0101://ImageLength
                if (TypeIndex == 3) {//Type 0003 Short
                    height = Get_Int_From_Input_Data(position_of_Data, size_of_type);
                    break;
                }
            case 0x0102://BitsPerSample
                for (int i = 0; i < Count; i++) {
                    int v = Get_Int_From_Input_Data(position_of_Data + i * size_of_type, size_of_type);
                    BitsPerSample.add(v);
                    PixelBytes += v / 8;
                }
                break;
//            case 0x0103: //Compression
//                Compression = GetInt(position_of_Data,size_of_type);break;
            case 0x0106: //Photometric Interpretation
                PhotometricInterpretation = Get_Int_From_Input_Data(position_of_Data, size_of_type);
                break;
            case 0x0111://StripOffsets
                for (int i = 0; i < Count; i++) {
                    int v = Get_Int_From_Input_Data(position_of_Data + i * size_of_type, size_of_type);
                    StripOffsets.add(v);
                }
                break;
//            case 0x0112: break;//Orientation
//            case 0x0115: break;//Samples Per Pixel
//            case 0x0116://Rows Per Strip
//                RowsPerStrip = GetInt(position_of_Data,size_of_type);
//                break;
            case 0x0117://Strip Byte Counts
                for (int i = 0; i < Count; i++) {
                    int v = Get_Int_From_Input_Data(position_of_Data + i * size_of_type, size_of_type);
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
                for (int i = 0; i < Count; i++) {
                    int v = Get_Int_From_Input_Data(position_of_Data + i * size_of_type, size_of_type);
                    SampleFormat.add(v);
                }
                break;

            default:
                break;
        }
    }

    public static void RGB_decoder() {
        int position = StripOffsets.get(0);
//        System.out.println(StripOffsets.size());
        for (int i = 0; i < height * width; i++) {
            int R = Byte.toUnsignedInt(input_data[position + 3 * i]);
            int G = Byte.toUnsignedInt(input_data[position + 1 + 3 * i]);
            int B = Byte.toUnsignedInt(input_data[position + 2 + 3 * i]);
//            input_str += (char)R + (char)G + (char)B;
            RGBvalue[i] = new Color(R, G, B);
        }
    }

    //    public static float GetRational(int startPosition){
//        int A = Get_Int_From_Input_Data(startPosition,4);
//        int B = Get_Int_From_Input_Data(startPosition+4,4);
//        return A / B;
//    }


    public static void load_RGBvalue_to_Yvalue() {
        int index = 0;//index of RGB value
        int Y = 0;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                Y = (int) (RGBvalue[index].getRed() * 0.299 + RGBvalue[index].getGreen() * 0.587 + RGBvalue[index].getBlue() * 0.114);
                input[i][j] = Y;
                Yvalue[index] = new Color(Y, Y, Y);
                index++;
            }
        }
    }

    public static void make_dither_matrix() {
        dither_matrix_width = 2;
        for (int i = 8; i >= 1; i--) {
            if (width % i == 0) {
                dither_matrix_width = i;
                break;
            }
        }
        System.out.println("dither_matrix_width: " + dither_matrix_width);
        dither_matrix_height = 2;
        for (int i = 8; i >= 1; i--) {
            if (height % i == 0) {
                dither_matrix_height = i;
                break;
            }
        }
        System.out.println("dither_matrix_width: " + dither_matrix_height);
        dither_matrix = new int[dither_matrix_height][dither_matrix_width];
        int[] buffer = new int[dither_matrix_height * dither_matrix_width];
        for (int i = 0; i < dither_matrix_height * dither_matrix_width; i += 2) {
            buffer[i] = i;
            if (i + 1 < dither_matrix_height * dither_matrix_width) {
                buffer[i + 1] = dither_matrix_height * dither_matrix_width - i - 1;
            }
        }
//        for(int i=0;i<dither_matrix_height*dither_matrix_width;i++){
//            buffer[i]=i;
//        }
        int index = 0;
        for (int i = 0; i < dither_matrix_height; i++) {
            for (int j = 0; j < dither_matrix_width; j++) {
                dither_matrix[i][j] = buffer[index++];
            }
        }
        printMatrix(dither_matrix, dither_matrix_height, dither_matrix_width);
    }

    public static void printMatrix(int[][] matrix, int rows, int columns) {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                System.out.print(matrix[i][j] + "\t");
            }
            System.out.print("\n\n");
        }
    }

    public static void ordered_dithering(int[][] input, int input_rows, int input_columns, int[][] output, int[][] ditherMatrix, int ditherMatrix_rows, int ditherMatrix_columns) {
        for (int i = 0; i < input_rows; i += ditherMatrix_rows) {
            for (int j = 0; j < input_columns; j += ditherMatrix_columns) {
                for (int k = 0; k < ditherMatrix_rows; k++) {
                    for (int l = 0; l < ditherMatrix_columns; l++) {
                        int value = input[i + k][j + l] / (256 / (ditherMatrix_rows * ditherMatrix_columns + 1));
                        if (value > ditherMatrix[k][l]) {
                            output[i + k][j + l] = 1;
                        } else {
                            output[i + k][j + l] = 0;
                        }
                    }
                }
            }
        }
        int index = 0;
        dither_result = new Color[input_columns * input_rows];
        for (int i = 0; i < input_rows; i++) {
            for (int j = 0; j < input_columns; j++) {
                if (output[i][j] == 1) {
                    dither_result[index++] = new Color(255, 255, 255);
                } else {
                    dither_result[index++] = new Color(0, 0, 0);
                }
            }
        }
    }

    public static void adjust_the_dynamic_range() {
        for (int i = 0; i < height * width; i++) {
            int R = RGBvalue[i].getRed();
            int G = RGBvalue[i].getGreen();
            int B = RGBvalue[i].getBlue();
            get_better_RGB(R, G, B, i);
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

    public static void get_better_RGB(int R, int G, int B, int index) {
        int Y = (int) (0.299 * R + 0.587 * G + 0.114 * B);
        int U = (int) (-0.299 * R - 0.587 * G + 0.886 * B);
        int V = (int) (0.701 * R - 0.587 * G - 0.114 * B);
        if (Y < (256 / 8)) {
            Y = (int) (Y * 1.4);
        } else if (Y > (256 / 8 * 6)) {
            Y = (int) (Y * 0.95);
        }
        R = Y + V;
        G = (int) (Y - 0.194 * U - 0.509 * V);
        B = Y + U;

        if (G < 0) {
            G = 0;
        } else if (G > 255) {
            G = 255;
        }

        if (R < 0) {
            R = 0;
        } else if (R > 255) {
            R = 255;
        }

        if (B < 0) {
            B = 0;
        } else if (B > 255) {
            B = 255;
        }
        //System.out.println(G);
        adjust_result[index] = new Color(R, G, B);
    }

    /////////////////////////  <----------  LZW Compression  ---------->  ////////////////////////////////////////////


    public static void LZWcoding(List<String> inputArr, List<String> lib, List<Integer> output) {
        int index_of_current_char = 0;
        String current_char = inputArr.get(index_of_current_char);
        int index_of_next_char = index_of_current_char + 1;
        String next_char;
        while (index_of_next_char < inputArr.size()) {
            System.out.println(index_of_current_char+"\n");
            next_char = inputArr.get(index_of_next_char);
            if (find_char(lib, current_char + next_char) != -1) {//exit
                current_char += next_char;
                index_of_next_char++;
            }
            else {
                output.add(find_char(lib, current_char));
                lib.add(current_char + next_char);
                current_char = next_char;
                index_of_current_char = index_of_next_char;
                index_of_next_char++;
            }
        }
        output.add(find_char(lib, current_char));
    }

    public static int find_char(List<String> lib, String value){
        for(int i = 0; i < lib.size(); i++){
            if(lib.get(i).equals(value)){
                return i;
            }
        }
        return -1;
    }

    public static String int_to_binary_string_8bits(int value){
        String temp = Integer.toBinaryString(value);
        int size = 8-temp.length();
        for(int i=0;i<size;i++){
            temp = "0"+temp;
        }
        return temp;
    }

//    public static void RGB_to_string_inputArr(Color[] RGBvalue, int width, int height,List<String> inputArr){
//    ////--------for LZW compression-------////
//        for(int i=0;i<width*height;i++){
//            String input = int_to_binary_string_8bits(RGBvalue[i].getRed());
//            input += int_to_binary_string_8bits(RGBvalue[i].getGreen());
//            input += int_to_binary_string_8bits(RGBvalue[i].getBlue());
//            for(int j=0;j<input.length();j++){
//                if(input.charAt(j) == '0'){
//                    inputArr.add("0");
//                }
//                else if(input.charAt(j) == '1'){
//                    inputArr.add("1");
//                }
//            }
//        }
//    }



    public static void RGB_to_string_inputArr(){
        for(int i=0;i<width*height;i++){
//            input_str += (char)(RGBvalue[i].getRed());
//            input_str += (char)(RGBvalue[i].getGreen());
//            input_str += (char)(RGBvalue[i].getBlue());
            huffman_input_str.append((char)(RGBvalue[i].getRed()));
            huffman_input_str.append((char)(RGBvalue[i].getGreen()));
            huffman_input_str.append((char)(RGBvalue[i].getBlue()));
        }
    }


    public static void LZWdecode(List<Integer> output, List<String> decode_result){
        int n = output.size();
         //定义键值对存放<code,string>
        List<String> reverted_lib = new ArrayList<String>();
        reverted_lib.add("0");
        reverted_lib.add("1");
        String s="NIL";
        int k;
        String str;
        for(int i=0;i<n;i++){
            k = output.get(i);
            boolean flag = (k < reverted_lib.size());
            if(flag){//若存在字典中，则输出值
                for(int j=0;j<reverted_lib.get(k).length();j++){
                    char temp = reverted_lib.get(k).charAt(j);
                    if(temp == '0'){
                        decode_result.add("0");
                    }
                    else{
                        decode_result.add("1");
                    }
                }
            }
            if(!s.equals("NIL")){
                String p = reverted_lib.get(k);
                if(!flag){
                    p = s + s.charAt(s.length()-1);
                }
                str = s;
                str += p.charAt(0);
                reverted_lib.add(str);
            }
            if(!flag){
                for(int j=0;j < (reverted_lib.get(k).length());j++){
                    char temp = reverted_lib.get(k).charAt(j);
                    if(temp == '0'){
                        decode_result.add("0");
                    }
                    else{
                        decode_result.add("1");
                    }
                }
            }
            s = reverted_lib.get(k);
        }
    }



/////////////////////////  <-------  Huffman Compression  ------->  ////////////////////////////////////////////

    public static byte[] huffman_compression(byte[] Bytes) {
        List<Node> nodes = getNodes(Bytes);
        Node Root = create_huffman_tree(nodes);
        Map<Byte, String> huffmanCodes = getCodes(Root);
        return compression(Bytes, huffmanCodes);
    }


    public static byte[] compression(byte[] bytes , Map<Byte,String> huffmanCodes) {
        StringBuilder str = new StringBuilder();
        for(byte b : bytes){
            str.append(huffmanCodes.get(b));
        }

        int len;
        if(str.length() % 8 == 0){
            len = str.length() / 8;
        }
        else{
            len = str.length() / 8 + 1;
        }

        int index = 0;
        byte[] huffmanCodeBytes = new byte[len];

        for(int i = 0; i < str.length(); i = i + 8){
            String strByte;
            if(i + 8 > str.length()){
                strByte = str.substring(i);
            }
            else{
                strByte = str.substring(i, i + 8);
            }
            huffmanCodeBytes[index] = (byte)Integer.parseInt(strByte,2);
            index++;
        }
        return huffmanCodeBytes;
    }

    public static byte[] huffman_decode(Map<Byte, String> huffmanCodes, byte[] huffmanBytes) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < huffmanBytes.length; i++) {
            byte b = huffmanBytes[i];
            boolean flag = (i == huffmanBytes.length - 1);
            stringBuilder.append(byte_to_bit_string(!flag, b));
        }

        Map<String, Byte> map = new HashMap<>();
        for (Map.Entry<Byte, String> entry : huffmanCodes.entrySet()) {
            map.put(entry.getValue(), entry.getKey());
        }

        List<Byte> list = new ArrayList<>();
        for (int i = 0; i < stringBuilder.length();) {
            int count = 1;
            boolean flag = true;
            Byte mybyte = null;

            while(flag){
                if(i+count >= stringBuilder.length()){
                    break;// bug fixed: sometimes it will miss the last byte data
                }
                String key = stringBuilder.substring(i, i + count);
                mybyte = map.get(key);
                if(mybyte == null){
                    count++;
                }
                else{
                    flag = false;
                }
            }
            list.add(mybyte);
            i += count;
        }
        byte[] b = new byte[list.size()];
        for(int i = 0; i < b.length; i++){
            if(list.get(i) == null){
                b[i] = (byte)0;
            }
            else{
                b[i] = list.get(i);
            }
        }
        return b;
    }

    public static String byte_to_bit_string(boolean check, byte my_byte) {
        int temp = my_byte;
        if(check){
            temp = temp | 256;
        }
        String str = Integer.toBinaryString(temp);
        if(check){
            return str.substring(str.length() - 8);
        }
        else{
            return str;
        }
    }

    public static Map<Byte, String> getCodes(Node root){
        if(root == null){
            return null;
        }
        getCodes(root.left_node, "0", stringBuilder);
        getCodes(root.right_node, "1", stringBuilder);
        return huffmanCodes;
    }

    public static void getCodes(Node node, String code, StringBuilder stringBuilder){
        StringBuilder str = new StringBuilder(stringBuilder);
        str.append(code);
        if(node != null){
            if(node.data == null){
                getCodes(node.left_node, "0", str);
                getCodes(node.right_node, "1", str);
            }
            else{
                huffmanCodes.put(node.data, str.toString());
            }
        }
    }

    public static List<Node> getNodes(byte[] bytes) {
        List<Node> nodes = new ArrayList<>();

        Map<Byte, Integer> counts = new HashMap<>();
        for(Byte b : bytes){
            Integer count = counts.get(b);
            if(count == null){
                counts.put(b, 1);
            }
            else{
                counts.put(b, count+1);
            }
        }

        for (Map.Entry<Byte, Integer> entry : counts.entrySet()) {
            nodes.add(new Node(entry.getKey(), entry.getValue()));
        }
        return nodes;
    }

    public static Node create_huffman_tree(List<Node> nodes) {
        while (nodes.size() > 1) {
            Collections.sort(nodes);
            Node leftNode = nodes.get(0);
            Node rightNode = nodes.get(1);
            Node parent = new Node(null,leftNode.weight+rightNode.weight);
            parent.left_node = leftNode;
            parent.right_node = rightNode;
            nodes.remove(leftNode);
            nodes.remove(rightNode);
            nodes.add(parent);
        }
        return nodes.get(0);
    }


//    // <------ DCT Coefficients ------>
//
//    public static void make_matrix_T(){
//        int size = 8;
//        double pi = 3.1415926;
//        for(int j=0;j<size;j++){
//            matrix_T[0][j] = sqrt(1.0/size);
//        }
//        for(int i=1;i<size;i++){
//            for(int j=0;j<size;j++){
//                matrix_T[i][j] = sqrt(2.0/size) * cos((2.0*j+1)*i*pi/(2.0*size));
//            }
//        }
//    }
//
//    public static void make_T_transpose(){
//        int size = 8;
//        for(int i=0;i<size;i++){
//            for(int j=i;j<size;j++){
//                matrix_T_transpose[i][j] = matrix_T[j][i];
//            }
//        }
//    }


/////////////////////////  <--------  My own Compression  -------->  ////////////////////////////////////////////

    public static void my_own_Compression(int ratio){
        int block_size = ratio*2;
        for(int i=0;i<height;i+=block_size){
            for(int j=0;j<width;j+=block_size){
                int R_sum_in_block = 0;
                int G_sum_in_block = 0;
                int B_sum_in_block = 0;
                int count = 0;
                for(int k=i;((k<i+block_size) && (k<height));k++){
                    for(int l=j;((l<j+block_size) && (l<width));l++){
                        R_sum_in_block += RGBvalue[k*width+l].getRed();
                        G_sum_in_block += RGBvalue[k*width+l].getGreen();
                        B_sum_in_block += RGBvalue[k*width+l].getBlue();
                        count++;
                    }
                }
                int R_average_in_block = R_sum_in_block/count;
                int G_average_in_block = G_sum_in_block/count;
                int B_average_in_block = B_sum_in_block/count;
                for(int k=i;((k<i+block_size) && (k<height));k++){
                    for(int l=j;((l<j+block_size) && (l<width));l++){
                        my_compression_R[k][l] = R_average_in_block;
                        my_compression_G[k][l] = G_average_in_block;
                        my_compression_B[k][l] = B_average_in_block;
                    }
                }
            }
        }
        for(int i=0;i<width*height;i++){
            output_Color_arr_temp[i] = new Color(my_compression_R[i/width][i%width],my_compression_G[i/width][i%width],my_compression_B[i/width][i%width]);
        }
    }

    public static List<Integer> RLE_compression(){ // return a int arr, need to convert to byte arr.
        int R_value = output_Color_arr_temp[0].getRed();
        int G_value = output_Color_arr_temp[0].getGreen();
        int B_value = output_Color_arr_temp[0].getBlue();
        int num_of_R_repeat = 1;
        int num_of_G_repeat = 1;
        int num_of_B_repeat = 1;
        List<Integer> compression_result = new ArrayList<Integer>();
        for(int i=1;i<height*width;i++){
            if(R_value == output_Color_arr_temp[i].getRed()){
                num_of_R_repeat++;
                if(num_of_R_repeat == 256){
                    compression_result.add(num_of_R_repeat);
                    compression_result.add(R_value);
                    num_of_R_repeat = 1;
                }
            }
            else{
                compression_result.add(num_of_R_repeat);
                compression_result.add(R_value);
                num_of_R_repeat = 1;
                R_value = output_Color_arr_temp[i].getRed();
            }
            if(i+1 == height*width){
                compression_result.add(num_of_R_repeat);
                compression_result.add(R_value);
            }
        }
        for(int i=1;i<height*width;i++){
            if(G_value == output_Color_arr_temp[i].getGreen()){
                num_of_G_repeat++;
                if(num_of_G_repeat == 256){
                    compression_result.add(num_of_G_repeat);
                    compression_result.add(G_value);
                    num_of_G_repeat = 1;
                }
            }
            else{
                compression_result.add(num_of_G_repeat);
                compression_result.add(G_value);
                num_of_G_repeat = 1;
                G_value = output_Color_arr_temp[i].getGreen();
            }
            if(i+1 == height*width){
                compression_result.add(num_of_G_repeat);
                compression_result.add(G_value);
            }
        }
        for(int i=1;i<height*width;i++){
            if(B_value == output_Color_arr_temp[i].getBlue()){
                num_of_B_repeat++;
                if(num_of_B_repeat == 256){
                    compression_result.add(num_of_B_repeat);
                    compression_result.add(B_value);
                    num_of_B_repeat = 1;
                }
            }
            else{
                compression_result.add(num_of_B_repeat);
                compression_result.add(B_value);
                num_of_B_repeat = 1;
                B_value = output_Color_arr_temp[i].getBlue();
            }
            if(i+1 == height*width){
                compression_result.add(num_of_B_repeat);
                compression_result.add(B_value);
            }
        }
//        System.out.println("size "+compression_result.size());
        return compression_result;
    }

    public static byte[] int_arr_to_byte_arr (List<Integer> arr){
//        byte[] output = new byte[arr.size()/2*5];
//        int index_of_output = 0;
//        for(int i=0;i<arr.size();i++){
//            if(i%2 == 0){
//                ByteBuffer buffer = ByteBuffer.allocate(4);
//                buffer.putInt(arr.get(i));
//                for(int j=0;j<4;j++){
//                    output[index_of_output+j] = buffer.get(j);
//                }
//                index_of_output+=4;
//            }
//            else{
//                output[index_of_output] = (byte) ((int)arr.get(i));
//                index_of_output ++;
//            }
//        }
        byte[] output = new byte[arr.size()];
        for(int i=0;i<arr.size();i++){
            output[i] = (byte) ((int)arr.get(i));
        }
        return output;
    }

    public static Color[] my_decode(byte[] input){
        Color [] result = new Color[width*height];
        List<Integer> R_arr = new ArrayList<Integer>();
        int R_repeat_count = 0;
        int R = 0;
        List<Integer> G_arr = new ArrayList<Integer>();
        int G_repeat_count = 0;
        int G = 0;
        List<Integer> B_arr = new ArrayList<Integer>();
        int B_repeat_count = 0;
        int B = 0;
        int index_of_input = 0;
        for(;R_arr.size()<width*height;index_of_input+=2){
            R_repeat_count = Byte.toUnsignedInt(input[index_of_input]);
            R = Byte.toUnsignedInt(input[index_of_input+1]);
            for(int j=0;j<R_repeat_count;j++){
                R_arr.add(R);
            }
        }

        for(;G_arr.size()<width*height;index_of_input+=2){
            G_repeat_count = Byte.toUnsignedInt(input[index_of_input]);
            G = Byte.toUnsignedInt(input[index_of_input+1]);
            for(int j=0;j<G_repeat_count;j++){
                G_arr.add(G);
            }
        }

        for(;B_arr.size()<width*height;index_of_input+=2){
            B_repeat_count = Byte.toUnsignedInt(input[index_of_input]);
            B = Byte.toUnsignedInt(input[index_of_input+1]);
            for(int j=0;j<B_repeat_count;j++){
                B_arr.add(B);
            }
        }

        for(int i=0;i<width*height;i++){
            result[i] = new Color(R_arr.get(i),G_arr.get(i),B_arr.get(i));
        }

        return result;
    }
}

