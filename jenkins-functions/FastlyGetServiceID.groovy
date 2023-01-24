#!/usr/bin/env groovy

def call(service){
    //Evaluating service id based on service name
    service = service.toLowerCase()
    
    switch(service) {
    case 'dev1-web':
        return("3xUWgW5RQn26J85Lnfo2pE");
        break;
    case 'dev2-web':
        return("3jCanteWpn7mnzHv9mb0o");
        break;
    case 'dev3-web':
        return("5twEDLYalbeLqk7HMPTmbN");
        break;
    case 'dev4-web':
        return("2xoiHvEHHbarT6oMToQkha");
        break;
    case 'dev5-web':
        return("6kza3gRh5yEgZuVaWMpkyR");
        break;
    case 'dev6-web':
        return("5HEQkGLqEzUyhyjCUpCRyV");
        break;
    case 'dev7-web':
        return("5rz6eMjQ9UnAULNBbU5sae");
        break;
    case 'dev8-web':
        return("3PB3JE5NIrop0qcaEaQlnh");
        break;
    case 'dev9-web':
        return("5x6w9OTJXRP8WjKszIDVOi");
        break;
    case 'dev10-web':
        return("3PDJQpIBn8GZps5hSMXOPV");
        break;
    case 'dev11-web':
        return("7LXYeOY29OeDSqx5QZzgzz");
        break;
    case 'integration-web':
        return("4rdOM4npdEbNeEF191nIyO");
        break;
    case 'regression-web':
        return("2kwGcGU6mWroOIBjleTcCO");
        break;
    case 'www-prd':
        return("iJJg0RcxgDM6qiGAPvevY");
        break;
    default:
        error("Service not found");
        break;
    }
}