const gulp = require('gulp'),
   connect = require('gulp-connect');

gulp.task('webserver', function () {
   connect.server({
      livereload: true,
      root: 'app/gentelella-master/production/',
      port: 8080
   });
});

gulp.task('default', ['webserver']);