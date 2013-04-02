var jsdom = require("jsdom");
var http = require('http');

var username = "XXXX";
var password = "XXXXXXXX";

var urlCas = 'https://login.insa-lyon.fr/'
var urlStage = 'http://intranet-if.insa-lyon.fr/stages/Listestage.php'

var serverPort = 6789;
	
http.createServer(function(request, response) {
    
	if(request.url == '/fetchStages') {
		RetrieveStages(function(jsonStages) {
			response.writeHead(200, {
				'Content-Type': 'application/json'
			});
			response.end(JSON.stringify(jsonStages));
			response.close();
		});
	}
}).listen(serverPort);


/**
 * URI-encode un objet
 * Param :
 *	- obj : 	Objet à serialiser
 *	- prefix : 	Préfixe optionnel
 * Retour : String uri-encodée
 */
function EncodeURIObject(obj, prefix) {
    var str = [];
    for(var p in obj) {
        var k = prefix ? prefix + "[" + encodeURIComponent(p) + "]" : encodeURIComponent(p), v = obj[p];
        str.push(typeof v == "object" ? 
            UrlEncodeObject(v, k) :
            encodeURIComponent(k) + "=" + encodeURIComponent(v));
    }
    return str.join("&");
}

/**
 * Permet de s'authentifier avec le cas de l'insa de Lyon.
 * Param :
 *	- username : 	Username valide
 *	- password : 	MdP correspondant
 *	- url : 		URL du CAS à utiliser
 *	- callback : 	Callback une fois la procédure finie
 * Retour : /
 */
function CasAuthentification( username, password,  url, callback) {

    var formulaire = [	'username' : username, 
						'password' : password, 
						'_eventId' : 'submit',
						'lt' : '' 
	];
	
	jsdom.env(
		url,
		function (errors, window) {
			// On recupère les valeurs manquantes pour notre formulaire : 
			formulaire['lt'] = window.document.querySelector('input[name="lt"]').value;
			url = window.document.getElementById('#login_form').action;
			if (url.indexOf("?") != -1) { url += "&"; }
			else { url += "?"; }
			url += EncodeURIObject(formulaire);
			// On  peut maintenant effectuer la connexion :
			jsdom.env(
				url,
				callback
			);
		}
	);
}

/**
 * Parse la page des stages pour stocker les informations dans un JSON.
 * Param :
 *	- dom : DOM de la page à parser
 *	- url : URL de la page, pour traiter les chemins relatifs
 * Retour : JSON contenant les données des stages
 */
function ParseStage( dom, url ) {
	var arrayLien = url.split('/');
	arrayLien.pop();
	var lienDossier = arrayLien.join("/")+"/";
	
	// On commence par recuperer le lien relatif vers le dossier contenant les docs de description. Pour cela on recupere le 1er lien qui vient et ne garde que le chemin vers le dossier en question:
	lienDocRegex =  new RegExp( 'Description</i> : <a href="(.*)"' );
	arrayLien = lienDocRegex.exec(dom.innerHTML)[1].split('/');
	arrayLien.pop();
	lienDossier += arrayLien.join("/")+"/";
	var jsonStages = {
		dossierDescriptions : lienDossier,
		stages : []};
	var titres = dom.getElementsByTagName('h3');
	for (var i = titres.length; i--;) {
		// Le DOM est immonde, donc la recuperation aussi:
		var contactNode = titres[i].nextSibling.nextSibling.nextSibling;
		var sujetNode = contactNode.nextSibling.nextSibling.nextSibling.nextSibling.nextSibling.nextSibling;
		var descriptionNode = sujetNode.nextSibling.nextSibling.nextSibling.nextSibling;
		if (sujetNode.nodeName == '#text') { descriptionNode = descriptionNode.nextSibling; } // S'il y a un sujet, ca fait un noeud de plus.
		var noteNode = null;
		if (descriptionNode.nextSibling.nextSibling.nextSibling.nodeName == 'I') {
			noteNode = descriptionNode.nextSibling.nextSibling.nextSibling.nextSibling.nextSibling.nextSibling;
		}
		
		var entrEtLieu = titres[i].innerText.split('Entreprise : ')[1].split(/[^\w]{3}/);
		jsonStages.stages.push({
			annee: titres[i].innerText.split('IF')[0],
			entreprise: entrEtLieu[0],
			lieu: entrEtLieu[1],
			contact: contactNode.nodeValue.length > 3? contactNode.nodeValue.split(': ')[1] : null,
			sujet: (sujetNode.nodeName == '#text')? sujetNode.nodeValue : null,
			description: descriptionNode.innerText,
			notes: noteNode? noteNode.nodeValue : null
		});
	}
	return jsonStages;
}

/**
 * Service asynchrone récupérant les données de stages sur l'intranet IF au format JSON. 
 * Param :
 *	- callback : Callback recevant en paramètre le JSON des stages.
 * Retour : /
 */
function RetrieveStages(callback) {
	CasAuthentification(username, password,  urlCas, function() {
		// Une fois connecté, on recupere la page des stages :
		jsdom.env(
			urlStage,
			function (errors, window) {
				if (errors) {
					callback(errors);
				}
				else {
					// Et on la parse avant de retourner le tout :
					callback(ParseStage( window.document.getElementsByTagName('body')[0], urlStage )); 
				}
			}
		);
	})
}
