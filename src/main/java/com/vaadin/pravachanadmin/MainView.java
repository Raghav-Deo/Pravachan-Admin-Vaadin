/**
 * 
 */
package com.vaadin.pravachanadmin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.FileBuffer;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.Route;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

/**
 * @author raghav
 *
 */

@Route(value = "/")
public class MainView extends VerticalLayout {
	Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
			"cloud_name", "belsare-pravachan",
			"api_key", "698568657444722",
			"api_secret", "YRq2-MCWUXFCpqCBBWuwcQ4Dj_k",
			"secure", true));
//	MemoryBuffer memoryBuffer = new MemoryBuffer();
	Paragraph result = new Paragraph("Your result will be shown here");
	FileBuffer fileBuffer = new FileBuffer();
	Upload audio_uploader = new Upload(fileBuffer);
	
	public MainView() {
		audio_uploader.setAcceptedFileTypes("audio/mp3", ".mp3");
		audio_uploader.setDropAllowed(true);
		audio_uploader.addStartedListener(event -> {
			Notification notification = Notification.show("Started the audio upload");
			notification.addThemeVariants(NotificationVariant.LUMO_PRIMARY);
		});
		audio_uploader.addFileRejectedListener(event -> {
			Notification notification = Notification.show("Your file was rejected by server");
			notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
		});
		audio_uploader.addFailedListener(event -> {
			Notification notification = Notification.show("File failed to upload");
			notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
		});
		audio_uploader.addSucceededListener(event -> {
			String public_id = "something";
//			InputStream audio_bytes = memoryBuffer.getInputStream();
			try {
				File audio_file = File.createTempFile("tmp_" + event.getFileName(), ".mp3");
				FileUtils.copyInputStreamToFile(fileBuffer.getInputStream(), audio_file);
				cloudinary.uploader().upload(audio_file, ObjectUtils.asMap(
					"resource_type", "raw",
					"public_id", public_id
				));
			} catch (IOException e) {
				Notification notification = Notification.show("An io error occcured");
				notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
				e.printStackTrace();
			}
			result.setText("Your file has been uploaded. This is your file's public id (Keep it safe) : " + public_id);
			Notification notification = Notification.show("File has been uploaded");
			notification.addThemeVariants(NotificationVariant.LUMO_CONTRAST);
		});
		add(
			new Paragraph("Upload your file with below feature."),
			audio_uploader,
			result
			);
	}

}
