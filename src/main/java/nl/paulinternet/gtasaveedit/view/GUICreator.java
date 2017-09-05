package nl.paulinternet.gtasaveedit.view;

import java.awt.Toolkit;

import javax.swing.*;

import nl.paulinternet.gtasaveedit.model.Settings;
import nl.paulinternet.gtasaveedit.view.window.MainWindow;

class GUICreator implements Runnable
{
	private boolean secondTime;
	
	public void run () {
		if (!secondTime) {
			// Set eventqueue to display errors
			Toolkit.getDefaultToolkit().getSystemEventQueue().push(new EventQueueProxy());

			// Set look and feel
			try {
				UIManager.setLookAndFeel(Settings.getLookAndFeelClassName());
				SwingUtilities.updateComponentTreeUI(MainWindow.getInstance());
			}
			catch (Exception ignored) {}
			
			// Create a window
			MainWindow.getInstance().setVisible(true);
			
			secondTime = true;
		}
		else {
			MainWindow.getInstance().createContent();
		}
	}
}
