find . -name "*.java" -exec sed -i s/R\.h\./R.i./ {} \; 
find . -name "*.java" -exec sed -i s/R\.d\./R.e./ {} \; 
find . -name "*.java" -exec sed -i s/R\.g\./R.h./ {} \;
find . -name "*.kt" -exec sed -i s/R\.h\./R.i./ {} \; 
find . -name "*.kt" -exec sed -i s/R\.d\./R.e./ {} \; 
find . -name "*.kt" -exec sed -i s/R\.g\./R.h./ {} \; 