package com.tgedr.labs.microservices.blueprint.model;

import java.io.Serializable;

public enum Status implements Serializable {
	submitted, started, completed, failed;
}
