/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.veggiebook.android.util.cropimage;

import android.graphics.Bitmap;
import android.net.Uri;

import java.io.InputStream;

//  Copyright © 2020 Quick Help For Meals, LLC. All rights reserved.
//
//  This file is part of VeggieBook.
//
//  VeggieBook is free software: you can redistribute it and/or modify
//  it under the terms of the GNU General Public License as published by
//  the Free Software Foundation, version 3 of the license only.
//
//  VeggieBook is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or fitness for a particular purpose. See the
//  GNU General Public License for more details.


/**
 * The interface of all images used in gallery.
 */
public interface IImage {
    static final int THUMBNAIL_TARGET_SIZE = 320;
    static final int MINI_THUMB_TARGET_SIZE = 96;
    static final int UNCONSTRAINED = -1;

    /** Get the image list which contains this image. */
    public abstract IImageList getContainer();

    /** Get the bitmap for the full size image. */
    public abstract Bitmap fullSizeBitmap(int minSideLength,
                                          int maxNumberOfPixels);
    public abstract Bitmap fullSizeBitmap(int minSideLength,
                                          int maxNumberOfPixels, boolean rotateAsNeeded);
    public abstract Bitmap fullSizeBitmap(int minSideLength,
                                          int maxNumberOfPixels, boolean rotateAsNeeded, boolean useNative);
    public abstract int getDegreesRotated();
    public static final boolean ROTATE_AS_NEEDED = true;
    public static final boolean NO_ROTATE = false;
    public static final boolean USE_NATIVE = true;
    public static final boolean NO_NATIVE = false;

    /** Get the input stream associated with a given full size image. */
    public abstract InputStream fullSizeImageData();
    public abstract long fullSizeImageId();
    public abstract Uri fullSizeImageUri();

    /** Get the path of the (full size) image data. */
    public abstract String getDataPath();

    // Get/Set the title of the image
    public abstract void setTitle(String name);
    public abstract String getTitle();

    // Get metadata of the image
    public abstract long getDateTaken();

    public abstract String getMimeType();

    public abstract int getWidth();

    public abstract int getHeight();

    public abstract String getDisplayName();

    // Get property of the image
    public abstract boolean isReadonly();
    public abstract boolean isDrm();

    // Get the bitmap/uri of the medium thumbnail
    public abstract Bitmap thumbBitmap(boolean rotateAsNeeded);
    public abstract Uri thumbUri();

    // Get the bitmap of the mini thumbnail.
    public abstract Bitmap miniThumbBitmap();

    // Rotate the image
    public abstract boolean rotateImageBy(int degrees);

}
