//try {
//    File sd = Environment.getExternalStorageDirectory();
//    File data = Environment.getDataDirectory();
//    if (sd.canWrite()) {
//        String sourceImagePath= "/path/to/source/file.jpg";
//        String destinationImagePath= "/path/to/destination/file.jpg";
//        File source= new File(data, souceImagePath);
//        File destination= new File(sd, destinationImagePath);
//        if (source.exists()) {
//            FileChannel src = new FileInputStream(source).getChannel();
//            FileChannel dst = new FileOutputStream(destination).getChannel();
//            dst.transferFrom(src, 0, src.size());
//            src.close();
//            dst.close();
//        }
//} catch (Exception e) {}