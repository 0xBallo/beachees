const gulp = require('gulp'),
   connect = require('gulp-connect');

gulp.task('webserver', function () {
   connect.server({
      livereload: true,
      root: ['app/gentelella-master/'],
      port: 8000
   });
});

gulp.task('default', ['webserver']);