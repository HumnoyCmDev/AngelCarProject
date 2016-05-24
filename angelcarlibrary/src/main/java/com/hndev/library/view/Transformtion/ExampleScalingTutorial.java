/*
    protected void badButtonPressed() {
        final long startTime = SystemClock.uptimeMillis();

        // Part 1: Decode image
        Bitmap unscaledBitmap = BitmapFactory.decodeResource(getResources(), mSourceId);

        // Part 2: Scale image
        Bitmap scaledBitmap = Bitmap
                .createScaledBitmap(unscaledBitmap, mDstWidth, mDstHeight, true);
        unscaledBitmap.recycle();

        // Calculate memory usage and performance statistics
        final int memUsageKb = (unscaledBitmap.getRowBytes() * unscaledBitmap.getHeight()) / 1024;
        final long stopTime = SystemClock.uptimeMillis();

        // Publish results
        mResultView.setText("Time taken: " + (stopTime - startTime)
                + " ms. Memory used for scaling: " + memUsageKb + " kb.");
        mImageView.setImageBitmap(scaledBitmap);
    }

    */
/**
     * Invoked when pressing button for showing result of the "Fit" decoding
     * method
     *//*

    protected void fitButtonPressed() {
        final long startTime = SystemClock.uptimeMillis();

        // Part 1: Decode image
        Bitmap unscaledBitmap = ScalingUtilities.decodeResource(getResources(), mSourceId,
                mDstWidth, mDstHeight, ScalingLogic.FIT);

        // Part 2: Scale image
        Bitmap scaledBitmap = ScalingUtilities.createScaledBitmap(unscaledBitmap, mDstWidth,
                mDstHeight, ScalingLogic.FIT);
        unscaledBitmap.recycle();

        // Calculate memory usage and performance statistics
        final int memUsageKb = (unscaledBitmap.getRowBytes() * unscaledBitmap.getHeight()) / 1024;
        final long stopTime = SystemClock.uptimeMillis();

        // Publish results
        mResultView.setText("Time taken: " + (stopTime - startTime)
                + " ms. Memory used for scaling: " + memUsageKb + " kb.");
        mImageView.setImageBitmap(scaledBitmap);
    }

    */
/**
     * Invoked when pressing button for showing result of the "Crop" decoding
     * method
     *//*

    protected void cropButtonPressed() {
        final long startTime = SystemClock.uptimeMillis();

        // Part 1: Decode image
        Bitmap unscaledBitmap = ScalingUtilities.decodeResource(getResources(), mSourceId,
                mDstWidth, mDstHeight, ScalingLogic.CROP);

        // Part 2: Scale image
        Bitmap scaledBitmap = ScalingUtilities.createScaledBitmap(unscaledBitmap, mDstWidth,
                mDstHeight, ScalingLogic.CROP);
        unscaledBitmap.recycle();

        // Calculate memory usage and performance statistics
        final int memUsageKb = (unscaledBitmap.getRowBytes() * unscaledBitmap.getHeight()) / 1024;
        final long stopTime = SystemClock.uptimeMillis();

        // Publish results
        mResultView.setText("Time taken: " + (stopTime - startTime)
                + " ms. Memory used for scaling: " + memUsageKb + " kb.");
        mImageView.setImageBitmap(scaledBitmap);
    }*/
