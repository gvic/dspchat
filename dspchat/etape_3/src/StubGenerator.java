import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * 
 */

/**
 * @author victorinox
 * 
 */
public class StubGenerator {

	private static String superClass = "SharedObject";
	private static String handledObj = "obj";

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		if (args.length != 1) {
			System.out
					.println("Vous devez saisir le nom de la classe pour générer son stub :");
			System.out
					.println("Usage : java StubGenerator <une_classe> (sans l'extension)");
		}

		File f = new File(args[0] + ".java");
		System.out.println(f.toString());
		// if (!f.exists())
		// throw new Exception("Le fichier "+args[0]+".java n'existe pas!");

		generateStubClassCode(args);

	}

	private static void generateStubClassCode(String[] args) throws Exception {
		Class c = Class.forName(args[0]);
		String cName = c.getSimpleName();
		String stubClassName = cName + "_stub";
		String itfClassName = cName + "_itf";
		String code = "";
		code = code + "public class " + stubClassName + " extends "
				+ superClass + " implements " + itfClassName
				+ ", java.io.Serializable {" + "\n\n\t";
		code = code
				+ "private static final long serialVersionUID = 1L;\n\n\n\t";
		code = code + "/* Constructors */" + "\n\n\t";

		Constructor ctorlist[] = Class.forName(superClass)
				.getDeclaredConstructors();
		Class[][] constructorsParameterTypes = new Class[ctorlist.length][];

		for (int i = 0; i < ctorlist.length; i++) {

			Class[] cpt = new Class[ctorlist[i].getParameterTypes().length];
			cpt = ctorlist[i].getParameterTypes();
			constructorsParameterTypes[i] = new Class[ctorlist[i]
					.getParameterTypes().length];
			;
			code += "public " + stubClassName + "(";
			String superCode = " ";
			String var = "var";

			String code_superCode[] = generateArgumentList(cpt, true);

			code += code_superCode[0] + "){\n\t\t";
			code += "super(" + code_superCode[1] + ");\n\t}\n\n\t";

		}

		code += "\n\n\t/* Methods */\n\n\t";
		code += generateMethods(Class.forName(itfClassName)
				.getDeclaredMethods(), cName);

		code += "}";

		createNewFileClass(stubClassName, code);
	}

	private static String generateMethods(Method[] declaredMethods,
			String className) {
		String code = "";
		String var = "vloc";

		for (int i = 0; i < declaredMethods.length; i++) {

			Method method = declaredMethods[i];
			Class[] parametersTypes = method.getParameterTypes();
			String[] argList = generateArgumentList(parametersTypes, true);
			String declaredParamCode = argList[0];
			String undeclaredParamCode = argList[1];
			String returnType = method.getReturnType().toString();
			String returnCode = "";
			String indice = (new Integer(i)).toString();


			if (returnType.startsWith("class ")) {
				returnType = returnType.substring(6);
				returnCode = "return ";
			} else if (returnType.startsWith("interface ")){
				returnType = returnType.substring(10);
				returnCode = "return ";
			}


			code += "public " + returnType + " " + method.getName() + "("
					+ declaredParamCode + "){\n\t\t";

			String castCode = className + " " + var + " = (" + className + ") "
					+ handledObj + ";\n\t\t";

			code += castCode;
			code += returnCode + var + "." + method.getName() + "("
					+ undeclaredParamCode + ");\n\t}\n\n\t";

		}

		return code + "\n";
	}

	public static void createNewFileClass(String name, String code)
			throws IOException {
		// création du fichier nomClasse_stub.java et écriture du texte à
		// l'intérieur
		File f = new File(name + ".java");
		f.createNewFile();
		FileOutputStream fos = new FileOutputStream(f);
		fos.write(code.getBytes());
	}

	private static String[] generateArgumentList(Object[] ao,
			boolean constructor) {
		String[] code = { "", "" };
		String var = "var";
		String indice = "";

		for (int j = 0; j < ao.length; j++) {

			indice = (new Integer(j)).toString();

			if (j < ao.length - 1) {
				if (constructor) {
					code[1] += var + indice + ", ";
				}
				code[0] += ((Class) ao[j]).getName() + " " + var + indice
						+ ", ";
			}

			else {
				if (constructor) {
					code[1] += var + indice;
				}
				code[0] += ((Class) ao[j]).getName() + " " + var + indice;
			}

		}

		return code;
	}

}
