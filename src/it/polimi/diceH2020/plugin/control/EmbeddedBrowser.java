package it.polimi.diceH2020.plugin.control;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import java.net.HttpURLConnection;
import java.net.URL;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ProgressBar;


public final class EmbeddedBrowser {  
	private static final int BUFFER_SIZE = 4096;
	private String URL;
	
	public EmbeddedBrowser(String url){
		this.URL=url;
	}
    public final void launch(Composite container) {
     
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 1;
        container.setLayout(gridLayout);
        GridData data = new GridData();
        final Browser browser = new Browser(container, SWT.FILL);
        data = new GridData();
        data.horizontalAlignment = GridData.FILL;
        data.verticalAlignment = GridData.FILL;
        data.horizontalSpan = 3;
        data.grabExcessHorizontalSpace = true;
        data.grabExcessVerticalSpace = true;
        browser.setLayoutData(data);


        final ProgressBar progressBar = new ProgressBar(container, SWT.NONE);
        data = new GridData();
        data.horizontalAlignment = GridData.END;
        progressBar.setLayoutData(data);
        browser.addProgressListener(new ProgressListener() {
            public void changed(ProgressEvent event) {
                if (event.total == 0)
                    return;
                int ratio = event.current * 100 / event.total;
                progressBar.setSelection(ratio);
            }

            public void completed(ProgressEvent event) {
                progressBar.setSelection(0);
            }
        });
        browser.setUrl(URL);
        browser.addLocationListener(new LocationListener(){
			@Override
			public void changing(LocationEvent event) {
				if(event.location.equals("http://localhost:8000/")){
					browser.setUrl(URL);
				}
				if(event.location.contains("download/")){
		        	JFileChooser j = new JFileChooser();
		        	j.setDialogTitle("Choose folder to save file");
		        	j.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		        	int choice = j.showOpenDialog(null);
		        	
		        	if (choice!= JFileChooser.APPROVE_OPTION) return;
		        	String selectedFolder=j.getSelectedFile().getAbsolutePath();
		        	try {
		        		downloadFile(event.location,selectedFolder);
		        	} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		        }
			}

			@Override
			public void changed(LocationEvent arg0) {
				
			}
        	
        	
        	
        	
        });
        

    }
    
    /**
     * Downloads a file from a URL
     * @param fileURL HTTP URL of the file to be downloaded
     * @param saveDir path of the directory to save the file
     * @throws IOException
     */
    public static void downloadFile(String fileURL, String saveDir)
            throws IOException {
        URL url = new URL(fileURL);
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        int responseCode = httpConn.getResponseCode();
 
        // always check HTTP response code first
        if (responseCode == HttpURLConnection.HTTP_OK) {
            String fileName = "";
            String disposition = httpConn.getHeaderField("Content-Disposition");
 
            if (disposition != null) {
                // extracts file name from header field
                int index = disposition.indexOf("filename=");
                if (index > 0) {
                    fileName = disposition.substring(index + 10,
                            disposition.length() - 1);
                }
            } else {
                // extracts file name from URL
                fileName = fileURL.substring(fileURL.lastIndexOf("/") + 1,
                        fileURL.length());
            }
 
            // opens input stream from the HTTP connection
            InputStream inputStream = httpConn.getInputStream();
            String saveFilePath = saveDir + File.separator + fileName;
             
            // opens an output stream to save into file
            FileOutputStream outputStream = new FileOutputStream(saveFilePath);
 
            int bytesRead = -1;
            byte[] buffer = new byte[BUFFER_SIZE];
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
 
            outputStream.close();
            inputStream.close();
 
            JOptionPane.showMessageDialog(null, "Download Complete", "InfoBox: " , JOptionPane.INFORMATION_MESSAGE);

        } else {
            
        }
        httpConn.disconnect();
    }
    
    
    
    
    
}