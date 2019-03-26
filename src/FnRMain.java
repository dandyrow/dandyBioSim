import javax.swing.*;

public class FnRMain {

	public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch(Exception ignored){}
		new SimulatorView();
	}

}
