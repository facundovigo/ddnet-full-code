//Self-Executing Anonymous Function para DDNET UserAgent.
(function(ddUserAgent, $, undefined){

    // Private properties
	var _initialized = false;
    var _endpoint = 'ws://127.0.0.1:6015';
    var _options = null;
    var _invoker = null;
    var _waitingForApp = false;
    var _waitingForAppConfig = null;
    var _startAppTimeout = null;

    // Public properties
    ddUserAgent.version = "1.0";

    // Public methods
    ddUserAgent.initialize = function(options) {
    	_options = options || {
    		"master" : false
    	};
    	
    	if (_initialized)
    		throw new Error("DDNET UserAgent ya fue inicializado!");
    	
		// Indica si esta ventana/tab mantendrá el websocket para la app nativa DDNET UserAgent.
    	if (!_options.master) 
    		_options.master = false;
		
		if (_options.master) {
			_invoker = null;
			initializeUserAgentConnection();
		} else { 
			_invoker = window.opener.ddUserAgent;
		}
		
    	_initialized = true;
    }

    ddUserAgent.isInitialized = function() {
    	return _initialized && (_options.master ? true : _invoker.isInitialized());
    }
    
    ddUserAgent.isMaster = function() {
    	return _options.master;
    }
    
    ddUserAgent.isUserAgentActive = function() {    	
    	if (_options.master)
    		return _initialized && window.userAgentWebSocket && window.userAgentWebSocket != null && userAgentWebSocket.readyState == 1;
    	else 
    		return _invoker.isUserAgentActive();
    }

    ddUserAgent.isWaitingForAppToStart = function() {
    	return _waitingForApp;
    }
        
    ddUserAgent.startUserAgent = function(onStartedCallback, onTimeoutCallback) {
    	var theInvoker = _options.master ? ddUserAgent : _invoker; 

    	// Abortamos si ya se esta esperando el inicio de la app, o si la misma ya se encuentra activa.
    	if (theInvoker.isWaitingForAppToStart() || theInvoker.isUserAgentActive()) {
    		console.log('UserAgent iniciando o ya activo. Saliendo...');
    		return 0;
    	}
    	    	
    	theInvoker.launchUserAgent(onStartedCallback, onTimeoutCallback);    	
    	return 1;
    }
    
    ddUserAgent.launchUserAgent = function(onStartedCallback, onTimeoutCallback) {
    	_waitingForApp = true;
    	
    	_waitingForAppConfig = {
			onStartedCallback: onStartedCallback,  
			onTimeoutCallback: onTimeoutCallback
    	};
    	
    	_startAppTimeout = setTimeout(function() {
    		var timeoutCallback = ddUserAgent.stopLaunchAppWait();
    		if (timeoutCallback)
    			timeoutCallback();
    	}, 30000);
    	
    	location.href = 'http://190.210.189.236/ddnet-web/useragent/ddnet-useragent.jnlp';    	
    } 
    
    ddUserAgent.stopLaunchAppWait = function() {
		var callback = ddUserAgent.isUserAgentActive() ? 
				_waitingForAppConfig.onStartedCallback : 
				_waitingForAppConfig.onTimeoutCallback;

		_waitingForApp = false;
		_waitingForAppConfig = null;		
		clearTimeout(_startAppTimeout);
		_startAppTimeout = null;
		
		return callback;
    }
    
    ddUserAgent.showUserAgent = function() {
    	var theInvoker = _options.master ? ddUserAgent : _invoker; 
		return theInvoker.sendUserAgentCommand("show-window");
    }

    ddUserAgent.announceUser = function() {
    	var theInvoker = _options.master ? ddUserAgent : _invoker; 

    	var token = jQuery.cookie("JSESSIONID");
    	if (token) token = token.replace(/ /g, '+');
    	
    	return theInvoker.sendUserAgentCommand("announce-user", 
    		{ 
    			"login": window.userinfo.login,
    			"token": token,
    			"fullName": window.userinfo.lastName + ', ' + window.userinfo.firstName 
    		}
    	);				
    }

    ddUserAgent.dismissUser = function() {
    	var theInvoker = _options.master ? ddUserAgent : _invoker;
    	return theInvoker.sendUserAgentCommand("dismiss-user");	
    }

    ddUserAgent.sendUserAgentCommand = function(command, parameters) {
    	if (_options.master) {	    	
	    	if (!ddUserAgent.isInitialized())
	    		throw new Error("DDNET UserAgent no fue inicializado!");
	    	
	    	if (!ddUserAgent.isUserAgentActive())
	    		return false;
	    	
	    	var request = { "command": command };
	    	if (parameters)
	    		request.parameters = parameters;	
	
	    	window.userAgentWebSocket.send(JSON.stringify(request));
	    	return true;
	    } else {
	    	return _invoker.sendUserAgentCommand(command, parameters);
	    } 	    	
    }

    
    // Private methods
    function initializeUserAgentConnection() {
		if (_options.listenPort)
			_endpoint = 'ws://127.0.0.1:' + parseInt(_options.listenPort);

		if (window.MozWebSocket)
            window.WebSocket = window.MozWebSocket;
    	else if (!window.WebSocket)
            console.log('Este browser no soporta WebSockets. La conexión con DDNET UserAgent no será posible.');

    	if (window.WebSocket) {				
    		setInterval(checkUserAgentConnection, 10000);
    		setInterval(userAgentKeepAlive, 60000);
    	}
    	
    	checkUserAgentConnection();
    }
    
    function onUserAgentWebSocketConnected(e) {
    	console.log('DDNET UserAgent conectado');
    	var theInvoker = _options.master ? ddUserAgent : _invoker;
    	
    	if (theInvoker.isWaitingForAppToStart()) {
    		var startedCallback = theInvoker.stopLaunchAppWait();
    		
			theInvoker.announceUser();
			
			if (startedCallback)
				startedCallback();
    	}
    	
    	if (_options.onConnected)
    		_options.onConnected();    	
    }
    
    function onUserAgentWebSocketDisconnected(e) {
    	console.log('DDNET UserAgent desconectado');
    	
    	if (_options.onDisconnected)
    		_options.onDisconnected(e.code);    	
    }

    function onUserAgentWebSocketMessage(e) {
    	console.log('DDNET UserAgent: ' + e);
    }

    function onUserAgentWebSocketError(e) {
    	console.log('Error comunicando con DDNET UserAgent. Endpoint: ' + e.target.url);
    }

    function checkUserAgentConnection() {
    	try {
    		/*
    		WebSocket.readyState
    			0 - connection not yet established
    			1 - conncetion established
    			2 - in closing handshake
    			3 - connection closed or could not open
    		*/
    		if (!window.userAgentWebSocket || window.userAgentWebSocket == null || 
    				window.userAgentWebSocket.readyState == 0 || window.userAgentWebSocket.readyState == 3) {
    			console.log('Conectando con DDNET UserAgent...');
    			window.userAgentWebSocket = new WebSocket(_endpoint);
    			window.userAgentWebSocket.onopen = function(evt) { onUserAgentWebSocketConnected(evt); };
    			window.userAgentWebSocket.onclose = function(evt) { onUserAgentWebSocketDisconnected(evt); };
    			window.userAgentWebSocket.onmessage = function(evt) { onUserAgentWebSocketMessage(evt); };
    			window.userAgentWebSocket.onerror = function(evt) { onUserAgentWebSocketError(evt); };						
    		}						
    	} catch(err) {
    		console.log('Error verificando conexión con UserAgent: ' + err);
    	}
    }

    function userAgentKeepAlive() {
    	try {			
    		ddUserAgent.sendUserAgentCommand("keep-alive");
    	} catch(err) {
    		console.log('Error enviando KeepAlive a UserAgent: ' + err);
    	}
    }
    

})(window.ddUserAgent = window.ddUserAgent || {}, null);


//# sourceURL=ddweb-useragent.js