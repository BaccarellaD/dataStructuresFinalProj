package guiPack;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.File;

public class HundredKWord {

	public static void main(String[] args) {//used when creating the 100k word file to be used in the text generator, not really part of the program
		try{
			BufferedReader br = new BufferedReader(new FileReader(new File("WarAndPeaceFile/warAndPeace.txt")));
			StringBuilder sb = new StringBuilder();
			String line;
			while((line = br.readLine()) != null){
				sb.append(line + "\n");
			}
			int count = 0;
			br.close();
			Pattern p = Pattern.compile("(\\w*['’-]*\\w)+");
			Matcher m = p.matcher(sb.toString());
			sb = new StringBuilder();
			while(m.find()){
				//System.out.println("Group: " + m.group());
				if(count > 105000){
					break;
				}
				if(count > 5000){
					sb.append(m.group()+" ");
				}
				if(count % 10 == 0 && count > 5000){
					sb.append("\n");
				}
				count++;
			}
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File("generateTextFolder/warAndPeace100k.txt")));
			bw.write(sb.toString());
			bw.close();
			System.out.println("Done.");
		}catch(IOException e){
			e.printStackTrace();
		}

	}

}
