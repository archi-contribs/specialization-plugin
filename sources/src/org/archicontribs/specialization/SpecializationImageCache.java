package org.archicontribs.specialization;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Display;

import lombok.Getter;
import lombok.Setter;

public class SpecializationImageCache {
    /**
     * Used to cache the images as I do not know when they're going to be disposed
     */
    private List<Icon> iconsCache = new ArrayList<Icon>();
    
    public SpecializationImageCache() {
    }
    
    public void set(String name, Image image) {
		ImageData imageData = image.getImageData();
		int width = imageData.width;
		int height = imageData.height;
		
		for ( Icon icon: this.iconsCache ) {
			if ( icon.getIconName().equals(name) && (icon.getIconWidth() == width) && (icon.getIconHeight() == height) ) {
				icon.setIconImage(image);
				return;
			}
		}
    	
		// if we're here, the image does not exist in the cache
		this.iconsCache.add(new Icon(name, image, false));
    }
    
    public Image get(String name) {
    	return get(name, 0, 0);
    }
    
    public Image get(String name, int width, int height) {
    	Image unresizedImage = null;
    	
    	for ( Icon icon: this.iconsCache ) {
			if ( icon.getIconName().equals(name) ) {
				if ( !icon.isResized() ) {
					unresizedImage = icon.getIconImage();
					if ( (width == 0) && (height == 0) )
						return unresizedImage;
				}
				if ( (icon.getIconWidth() == width) && (icon.getIconHeight() == height) ) {
					return icon.getIconImage();
				}
			}
		}
    	
    	// else, we need to resize the image from the unresized one
    	if ( unresizedImage != null ) {
    		Image resizedImage = resizeImage(unresizedImage, width, height);
	    	if ( resizedImage != null ) {
	    		this.iconsCache.add(new Icon(name, resizedImage, true));
	    	   	return resizedImage;
	    	}
    	}
    	return unresizedImage;
    }
    
    public static Image resizeImage(Image image, int width, int height) {
    	if ( image == null )
    		return null;
    	
    	int imageWidth = (width <= 0 ? 0 : width);
    	int imageHeight = (height <= 0 ? 0 : height);
    	
    	ImageData imageData = image.getImageData();
    	
    	// if the new size is zero or the same as the original, then we do not resize 
    	if ( ((imageWidth == 0) || (imageWidth == imageData.width)) && ((imageHeight == 0) || (imageHeight == imageData.height)) )
    		return null;
    	
    	// if the width or the height is equal to zero (but not both), then we apply a ratio to the other dimension
    	if ( imageWidth == 0 )
    		imageWidth = (int)(imageData.width*(float)(imageHeight/imageData.height));
    	else if ( imageHeight == 0 )
    		imageWidth = (int)(imageData.height*(float)(imageWidth/imageData.width));
    	
    	// if the new size is the same as the original, then we do not resize 
    	if ( (imageWidth == imageData.width) && (imageHeight == imageData.height) )
    		return null;
    	
    	return new Image(Display.getCurrent(), imageData.scaledTo(imageWidth, imageHeight));
    }
    
    /*
     * remove the resized images from the cache
     */
    public void cleanupResizedImages() {
		for ( Icon icon: this.iconsCache ) {
			if ( icon.resized ) {
				this.iconsCache.remove(icon);
			}
		}
    }
    
    /*
     * remove the image from the cache
     */
    public void removeImage(String name) {
		for ( Icon icon: this.iconsCache ) {
			if ( icon.iconName.equals(name) ) {
				this.iconsCache.remove(icon);
			}
		}
    }
    
    private class Icon {
    	@Getter         String iconName;
    	@Getter @Setter Image iconImage;
    	@Getter         int iconWidth;
    	@Getter         int iconHeight;
    	@Getter @Setter boolean resized;
    	
    	public Icon(String name, Image image, boolean resized) {
    		this.iconName = name;
    		this.iconImage = image;
    		this.resized = resized;
    		
    		ImageData imageData = image.getImageData();
    		this.iconWidth = imageData.width;
    		this.iconHeight = imageData.height;
    	}
    }
}
