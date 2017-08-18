package com.ipinyou.data.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

import org.apache.commons.lang3.StringUtils;

import com.ipinyou.data.service.ApplicationProperties;
import com.ipinyou.data.service.MailProcedureService;
import com.ipinyou.data.utils.I18nResourceUtil;

public class InteractionInterface extends JFrame implements ActionListener {

	private static final JTextField AttachmentFileName = new JTextField();
	private static final JTextField MailTo = new JTextField();
	private static final JTextField MailSubject = new JTextField();
	private static final JTextArea MailDetailContent = new JTextArea();
	private static final JTextField MailTitles = new JTextField();
	private static final JTextArea Sql = new JTextArea();
	private static final JButton MailButton = new JButton();
	private static final JPanel jpn = new JPanel();
	private static final InteractionInterface itact = new InteractionInterface();
	private static final ApplicationProperties properties = new ApplicationProperties(false);
	private static final MailProcedureService procedure = new MailProcedureService();

	/**
	 * 
	 */
	private static final long serialVersionUID = -3130472197813232705L;

    public static void perform() {

		while (true) {
			JOptionPane localJOptionPane = new JOptionPane(null,
					JOptionPane.CLOSED_OPTION);
			localJOptionPane.add(MailDetailContent, JOptionPane.INFORMATION_MESSAGE);
			localJOptionPane.add(MailTitles, JOptionPane.INFORMATION_MESSAGE);
			localJOptionPane.add(MailSubject, JOptionPane.INFORMATION_MESSAGE);
			localJOptionPane.add(MailTo, JOptionPane.INFORMATION_MESSAGE);
			localJOptionPane.add(AttachmentFileName, JOptionPane.INFORMATION_MESSAGE);
			AttachmentFileName.setAlignmentX(JOptionPane.CENTER_ALIGNMENT);
			MailTo.setAlignmentX(JOptionPane.CENTER_ALIGNMENT);
			MailSubject.setAlignmentX(JOptionPane.CENTER_ALIGNMENT);
			MailDetailContent.setAlignmentX(JOptionPane.CENTER_ALIGNMENT);
			MailTitles.setAlignmentX(JOptionPane.CENTER_ALIGNMENT);
			AttachmentFileName.addFocusListener(customListener(AttachmentFileName
					, I18nResourceUtil.getResource("text.attachmentfile.name")));
			MailTo.addFocusListener(customListener(MailTo
					, I18nResourceUtil.getResource("text.mail.to")));
			MailSubject.addFocusListener(customListener(MailSubject
					, I18nResourceUtil.getResource("text.mail.subject")));
			MailTitles.addFocusListener(customListener(MailTitles
					, I18nResourceUtil.getResource("text.mail.title")));
			MailDetailContent.addFocusListener(customListener(MailDetailContent
					, I18nResourceUtil.getResource("text.mail.content")));
			MailDetailContent.setPreferredSize(new Dimension(550, 220));
			JDialog localJDialog = localJOptionPane.createDialog(
					localJOptionPane, I18nResourceUtil.getResource("dialog.title"));
			localJDialog.setSize(580, 460);
			localJDialog.setVisible(true);
			String fileName = String.valueOf(AttachmentFileName.getText());
			String mailTo = String.valueOf(MailTo.getText());
			String subject = String.valueOf(MailSubject.getText());
			String titles = String.valueOf(MailTitles.getText());
			String content = String.valueOf(MailDetailContent.getText());
			localJDialog.dispose();
			
			String prompt = null;
			int check = -1;
			if (StringUtils.isBlank(mailTo) || mailTo.startsWith("*")) {
				prompt = I18nResourceUtil.getResource("prompt.mail.to");
			} else if (StringUtils.isBlank(subject) || subject.startsWith("*")) {
				prompt = I18nResourceUtil.getResource("prompt.mail.subject");
			}
			if (StringUtils.isNotBlank(prompt)) {
				check = JOptionPane.showOptionDialog(null, prompt, I18nResourceUtil.getResource("dialog.prompt"),
						JOptionPane.YES_NO_CANCEL_OPTION,
						JOptionPane.INFORMATION_MESSAGE, null, new String[] {
								I18nResourceUtil.getResource("dialog.ensure"),
								I18nResourceUtil.getResource("dialog.cancel"),
								I18nResourceUtil.getResource("dialog.quit")},
								I18nResourceUtil.getResource("dialog.ensure"));
			}
			
			switch (check) {
			case 1:
				return;
			case 2:
				JOptionPane.showMessageDialog(null, I18nResourceUtil.getResource("application.exit.succ"), I18nResourceUtil.getResource("dialog.prompt"),
						JOptionPane.CLOSED_OPTION);
				System.exit(-1);
			default:
				break;
			}
			
			if (check == 0) {
				continue;
			}
			
			// 设置配置字段
			setProperties(fileName, mailTo, subject, titles, content);
			
			break;
		}
	
    }

    private static FocusListener customListener(final JTextComponent field, final String placeholder) {
    	final class TextFieldPlaceholderHandler {
    		public void done() {
    			field.setText(placeholder);
	        	field.setForeground(new Color(150, 150, 150));
    		}
    	}
    	final TextFieldPlaceholderHandler textFieldPlaceholderHandler = new TextFieldPlaceholderHandler();
    	textFieldPlaceholderHandler.done();
    	return new FocusListener() {
		    public void focusGained(FocusEvent e) {
		    	if (field.getForeground().getRed() == 150) {
		    		field.setText("");
		    		field.setForeground(new Color(50, 50, 50));
		    	}
		    }
		    public void focusLost(FocusEvent e) {
		        if (field.getText().length() == 0) {
		        	textFieldPlaceholderHandler.done();
		        }
		    }
		};
    }

    private static void setProperties(String fileName, String mailTo, String subject, String titles, String content) {
    	properties.setProperties(fileName, mailTo, subject, titles, content, null);
    	InteractionInterface.performSql();
    }

    public static void performSql() {
    	itact.setTitle(I18nResourceUtil.getResource("jFrame.sql.title"));
    	final JButton ensure = new JButton();
    	ensure.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	itact.remove(ensure);
            }
        });
    	JMenuBar jb = new JMenuBar();
    	itact.setJMenuBar(jb);
		Sql.addFocusListener(customListener(Sql, I18nResourceUtil.getResource("sql.prompt")));
		JScrollPane jsp = new JScrollPane(Sql);
		itact.setSize(600, 400);
		itact.setLayout(new BorderLayout());
		MailButton.setText(I18nResourceUtil.getResource("sql.ensure.button"));
		MailButton.addActionListener(itact);
		jpn.add(MailButton);
		itact.add(ensure);
		itact.add(jsp, BorderLayout.CENTER);
		itact.add(jpn, BorderLayout.SOUTH);
		itact.setVisible(true);
		itact.setLocationRelativeTo(null);
		itact.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {
//			new InteractionInterface();
//	    	perform();
    	String getenv = System.getenv("JAVA_HOME");
    	System.out.println(getenv);
	}

	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if (e.getSource() == MailButton) {
			this.setVisible(false);
			String prompt = null;
			String handleSQL = properties.handleSQL(Sql.getText());
			if (procedure.autoConduct(handleSQL, properties)) {
				prompt = I18nResourceUtil.getResource("finished.prompt");
			} else {
				prompt = I18nResourceUtil.getResource("failed.prompt");
			}
			int option = JOptionPane.showOptionDialog(null, prompt, I18nResourceUtil.getResource("dialog.prompt"),
					JOptionPane.YES_NO_CANCEL_OPTION,
					JOptionPane.INFORMATION_MESSAGE, null, new String[] {I18nResourceUtil.getResource("dialog.ensure")},
							I18nResourceUtil.getResource("dialog.ensure"));
			if (option == 0) {
				System.exit(0);
			}
		}
	}

}
