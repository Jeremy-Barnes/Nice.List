module.exports = function (grunt) {
	grunt.initConfig({
		uglify: {
			files: {
				src: 'Nice.ListFrontend/Nice.List/Nice.List/**/*.js',
				dst: 'jsout/',
				expand: true,
				flatten: true,
				ext: '.js'
			}
		},
		cssmin: {
			files: {
				src: 'Nice.ListFrontend/Nice.List/Nice.List/**/*.css',
				dst: 'jsout/',
				expand: true,
				flatten: true,
				ext: '.css'
			}
		}
	});

grunt.loadNpmTasks('grunt-contrib-uglify');
grunt.loadNpmTasks('grunt-contrib-cssmin');
	
grunt.registerTask('default', [ 'uglify' ]);

};