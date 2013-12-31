package de.pgalise.simulation.controlCenter.ctrl;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import de.pgalise.simulation.controlCenter.model.MapAndBusstopFileData;
import de.pgalise.simulation.shared.persistence.AbstractIdentifiable;
import java.util.LinkedList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;

/**
 *
 * @author richter
 */
@ManagedBean
@SessionScoped
public class StartParameterConstants extends AbstractIdentifiable
{

//	@ManagedProperty(value = "#{osmAndBusstopFileDataList}")
	private List<MapAndBusstopFileData> oSMAndBusstopFileDataList = new LinkedList<MapAndBusstopFileData>();

	public StartParameterConstants() {
	}

	public StartParameterConstants(
					List<MapAndBusstopFileData> oSMAndBusstopFileDatas) {
		this.oSMAndBusstopFileDataList = oSMAndBusstopFileDatas;
	}

	public List<MapAndBusstopFileData> getoSMAndBusstopFileDataList() {
		return oSMAndBusstopFileDataList;
	}

	public void setoSMAndBusstopFileDataList(
					List<MapAndBusstopFileData> oSMAndBusstopFileDatas) {
		this.oSMAndBusstopFileDataList = oSMAndBusstopFileDatas;
	}

	@PostConstruct
	public void init() {
		oSMAndBusstopFileDataList.add(
						new MapAndBusstopFileData());
	}
}
