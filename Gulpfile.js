const gulp = require('gulp'),
   connect = require('gulp-connect');

gulp.task('webserver', function () {
   connect.server({
      livereload: true,
      root: ['app/gentelella-master/'],
      port: 8000
   });
});

gulp.task('beacon-test', function () {
      connect.server({
         livereload: true,
         root: ['app/beacon-test/']
      });
})

gulp.task('default', ['webserver']);