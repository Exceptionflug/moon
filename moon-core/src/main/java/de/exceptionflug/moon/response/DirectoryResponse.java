package de.exceptionflug.moon.response;

import de.exceptionflug.moon.elements.simple.*;
import org.apache.http.client.utils.URIBuilder;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DirectoryResponse extends TextResponse {

    public DirectoryResponse(final File dir, URI request) {
        super("<html><head><meta charset=\"utf-8\"></head><body><h1>Contents of "+request.getPath()+"</h1>::%content%::<br>"+new MoonFooterElement()+"</body></html>", "text/html");
        if(!request.getPath().endsWith("/")) {
            try {
                request = new URIBuilder(request).setPath(request.getPath()+"/").build();
            } catch (final URISyntaxException e) {
            }
        }
        final TableElement tableElement = new TableElement(new TableElement.Row(Arrays.asList(new BoldElement(new BaseElement("File name")), new BoldElement(new BaseElement("Type")), new BoldElement(new BaseElement("Last modified")))));
        final List<File> sorted = new ArrayList<>(Arrays.asList(dir.listFiles()));
        sorted.sort((o1, o2) -> {
            if(o1.isDirectory() != o2.isDirectory()) {
                if(o1.isDirectory())
                    return -1;
                if(o2.isDirectory())
                    return 1;
            } else {
                return o1.getName().compareTo(o2.getName());
            }
            return 0;
        });
        if(sorted.isEmpty()) {
            replace("::%content%::", new BoldElement(new BaseElement("This directory is empty!")));
        } else {
            for(final File file : sorted) {
                try {
                    tableElement.getRows().add(new TableElement.Row(Arrays.asList(new LinkElement(new BaseElement(file.getName()), new URIBuilder(request).setPath(request.getPath()+file.getName()).build().toString()), new BaseElement(file.isDirectory() ? "Directory" : getFileType(file)), new BaseElement(DateFormat.getDateTimeInstance().format(new Date(file.lastModified()))))));
                } catch (final URISyntaxException e) {
                    Logger.getLogger(DirectoryResponse.class.getName()).log(Level.SEVERE, "Exception", e);
                }
            }
            replace("::%content%::", tableElement);
        }
    }

    private String getFileType(final File file) {
        try {
            return Files.probeContentType(file.toPath());
        } catch (final Exception e) {
            return "File";
        }
    }

}
