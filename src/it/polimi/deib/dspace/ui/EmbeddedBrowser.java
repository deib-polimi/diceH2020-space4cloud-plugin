package it.polimi.deib.dspace.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ProgressBar;

public final class EmbeddedBrowser {  
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

    }
}