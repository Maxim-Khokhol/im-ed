package com.proImg.image_editor.service;
import com.proImg.image_editor.entities.Collage;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Service
public class CollageService {


    public byte[] generateCollage(Map<String, Object> request, String fileType) {
        try {
            Map<String, Object> blockForCollage = (Map<String, Object>) request.get("blockForCollage");
            List<Map<String, Object>> imagesForCollage = (List<Map<String, Object>>) request.get("imagesForCollage");

            int width = (int) blockForCollage.get("width");
            int height = (int) blockForCollage.get("height");
            int topB = (int) blockForCollage.get("top");
            int leftB = (int) blockForCollage.get("left");
            String backgroundColor = (String) blockForCollage.get("backgroundColor");

            BufferedImage collage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = collage.createGraphics();

            Color bgColor = Color.decode(backgroundColor);
            graphics.setColor(bgColor);
            graphics.fillRect(0, 0, width, height);

            for (Map<String, Object> imageData : imagesForCollage) {
                String base64Image = (String) imageData.get("base64");
                int imgWidth = (int) imageData.get("width");
                int imgHeight = (int) imageData.get("height");
                int left = ((int) imageData.get("left")) - leftB;
                int top = ((int) imageData.get("top")) - topB;

                BufferedImage image = decodeBase64ToImage(base64Image);
                if (image != null) {
                    BufferedImage resizedImage = resizeImage(image, imgWidth, imgHeight);
                    graphics.drawImage(resizedImage, left, top, null);
                }
            }

            graphics.dispose();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            if (!ImageIO.getImageWritersByFormatName(fileType).hasNext()) {
                throw new IllegalArgumentException("Unsupported file type: " + fileType);
            }

            ImageIO.write(collage, fileType, baos);
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error while creating collage: " + e.getMessage(), e);
        }
    }

    private BufferedImage decodeBase64ToImage(String base64) {
        try {
            String base64Data = base64.split(",")[1];
            byte[] imageBytes = Base64.getDecoder().decode(base64Data);
            return ImageIO.read(new java.io.ByteArrayInputStream(imageBytes));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = resizedImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
        g2d.dispose();
        return resizedImage;
    }

}

