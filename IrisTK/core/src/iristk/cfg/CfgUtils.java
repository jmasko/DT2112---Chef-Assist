package iristk.cfg;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.bind.JAXBException;

public class CfgUtils {

	public static void srgs2abnf(String... args) throws FileNotFoundException, JAXBException {
		new ABNFGrammar(new SRGSGrammar(new File(args[0]))).marshal(System.out);
	}
	
	public static void abnf2srgs(String... args) {
		try {
			new SRGSGrammar(new ABNFGrammar(new File(args[0]))).marshal(System.out);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (GrammarException e) {
			e.printStackTrace();
		}
	}
	
	public static String srgs2abnf(File file) throws FileNotFoundException, JAXBException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		new ABNFGrammar(new SRGSGrammar(file)).marshal(out);
		return out.toString();
	}
	
	public static String abnf2srgs(File file) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			new SRGSGrammar(new ABNFGrammar(file)).marshal(out);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (GrammarException e) {
			e.printStackTrace();
		}
		return out.toString();
	}
	
}
