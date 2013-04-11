

function main(gb) {
	console.log(gb);
	for ( var i=0; i<gb.length; i++) {
		var content = '<div>['+gb[i].time+'] <strong>'+ gb[i].name + '</strong></div>';
		content += '<div>'+ gb[i].text +'</div>';
		content += '<hr/>'
		var div = $('<div></div>');
		div.attr('style', 'padding:10px;');
		div.append(content);
		console.log(div);
		div.appendTo('#content');
	}
}