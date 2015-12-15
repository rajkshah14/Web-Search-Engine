var substringMatcher = function(strs) {
	return function findMatches(q, cb) {
		var matches, substringRegex;
		matches = [];
		substrRegex = new RegExp(q, 'i');
		$.each(strs, function(i, str) {
			if (substrRegex.test(str)) {
				matches.push(str);
			}
		});
		cb(matches);
	};
};

$(document).ready(function(){
	var language = document.getElementById("language").options[document.getElementById("language").selectedIndex].text;
	var suggestions = new Bloodhound({
		datumTokenizer: Bloodhound.tokenizers.obj.whitespace('value'),
		queryTokenizer: Bloodhound.tokenizers.whitespace,
		remote: {
			url: '/project08/querysuggestion?language=' + language + '&query=%QUERY',
			wildcard: '%QUERY'
		}
	});

	$('#query').typeahead({
		hint: true,
		highlight: true,
		minLength: 1
	}, {
		name: 'suggestions',
		display: 'value',
		source: suggestions,
		async: true
	});


});


	$(".dropdown-menu li a").click(
		function () {
			var selText = $(this).text();
			$(this).parents('.dropdown').find('.dropdown-toggle').html(
				selText + '<span class="caret"></span>');
		});

