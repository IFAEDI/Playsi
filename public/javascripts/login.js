/**************************************************
* Author : Sébastien Mériot			              *
* Date : 25.03.2012				                  *
* Description : Gestion de l'authentification par *
* le CAS ou de façon plus classique (user/pass)	  *
**************************************************/

$(document).ready( function() {
	/* Enregistrement des handlers */
	$( "a#cas_login" ).click( cas_login );
	$( "a#regular_login" ).click( regular_login );

	$( "a#user_info_save" ).click( user_info_save );
} );

/**
* Permet l'authentification par le CAS
* <=> Balancer le form sur la page courante avec une variable cachée (voir le form)
*/
function cas_login() {
    // TODO cas login
	$( "#cas_login_form" ).submit();
}

/**
* Authentification de façon normale par user/mdp et en AJAX !
*/
function regular_login() {
	
	/* Vérification que les champs sont bien remplis */
	var username = $( "#login_form #username" ).val();
	var password = $( "#login_form #password" ).val();

	$( "#login_form fieldset" ).children( ".control-group" ).removeClass( "error" );

	if( password.length == 0 || username.length == 0 ) {
		if( username.length == 0 ) {
			$( "#login_form #username" ).parent().parent().addClass( "error" );
		}

		if ( password.length == 0 ) {
			$( "#login_form #password" ).parent().parent().addClass( "error" );
		}

		$( "#login_form #login_error" ).html( "Merci de remplir les champs ci-dessous." );
		$( "#login_form #login_error" ).slideDown();

		return;
	}

	/* Envoi des données */
    jsRoutes.controllers.StaticPages.login(username, hex_sha1(password)).ajax({
        success: function( msg ) {
            if( msg.statut == "ok" ) {
                document.location = '/';
            }
            else {
                $( "#login_form #login_error" ).html( msg.mesg );
                $( "#login_form #login_error" ).slideDown();
            }
        },
        error: function( obj, ex, msg ) {
            // TODO standardiser les messages d'erreur
            alert( ex + ' - ' + msg + '\n' + obj.responseText );
        }
    });
}

/**
* Sauvegarde les informations relatives à l'utilisateur courant
*/
function user_info_save() {

	var password = $( "#user_info_form #password" ).val();
	var nom	     = $( "#user_info_form #nom" ).val();
	var prenom   = $( "#user_info_form #prenom" ).val();

	$( "#user_info_form fieldset" ).children( ".control-group" ).removeClass( "error" );

	/* Vérifie si password existe ou pas */
	if( password == void 0  || password.length == 0 ) {
		/* Il n'existe pas alors on lui met une vide */
		password = '';
	}
	else {
		/* Sinon encodage en SHA1 pour le changer */
		password = hex_sha1(password);
	}

	/* On formate la liste des mails et leurs libellés */
    var mails_array = [];
    var i = 0;
    $( "#user_info_form .libelle_mail" ).each( function() {

            //mails_array[i] = [$(this).val(), ''];
            // TODO créer prototype mail et prototype telephone
            mails_array[i] = {
                intitule: $(this).val(),
                email: ""
            };
            i++;
    } );

	i = 0;
	$( "#user_info_form .mail" ).each( function() {

		//mails_array[i][1] = $(this).val();
        mails_array[i].email = $(this).val();
		i++;
	} );

	/* On formate la liste des téléphones et leurs libellés */
    var telephones_array = [];
    var i = 0;
    $( "#user_info_form .libelle_telephone" ).each( function() {

            //telephones_array[i] = [$(this).val(), ''];
            telephones_array[i] = {
                intitule: $(this).val(),
                numero: ""
            }
            i++;
    } );

    var i = 0;
    $( "#user_info_form .telephone" ).each( function() {

            //telephones_array[i][1] = $(this).val();
            telephones_array[i].numero = $(this).val();
            i++;
    } );

	/* Préparation des données à balancer */
	$.ajax( {
        contentType: "application/json",
        type: "POST",
        dataType: "json",
        url: "/user",
        data: JSON.stringify({
            password   : password,
            nom        : nom,
            prenom     : prenom,
            mails      : mails_array,
            telephones : telephones_array
        }),

        success: function( msg ) {

            if( msg.statut == "ok" ) {
                $( "#navbar_username" ).html( ' ' + prenom + ' ' + nom );
                $( "#user_info_dialog" ).modal( 'hide' );
                document.location.reload();
            } else {
                $( "#user_info_form #user_info_error" ).addClass( 'alert-error' );

                $( "#user_info_form #user_info_error" ).html( msg.mesg );
                $( "#user_info_form #user_info_error" ).slideDown();
            }

        },
        error: function( obj, ex, msg ) {

                alert( ex + ' - ' + msg + '\n' + obj.responseText );
        }

	} );
}
