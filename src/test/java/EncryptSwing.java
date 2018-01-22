import javax.swing.JFrame;

public class EncryptSwing extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6168736001318222750L;

	public EncryptSwing() {
		setTitle("DES加解密工具");
		setSize(200, 200);
		setVisible(true);
	}
	
	public static void main(String[] args) {
		new EncryptSwing();
	}
}
