/ SPDX-License-Identifier: GPL-3.0

pragma solidity ^0.8.17; contract Details
{
  address public owner;
  
  struct Patient
  {
    string name; uint age; string contact;
    string medicine; string predooc; string hosp; string med;
    uint No_of_days;
  }

  Patient p;
  constructor()
  {
    owner=msg.sender;
  }
  
  modifier Onlyowner()
  {
    require(msg.sender==owner,"ERROR ERROR ERROR");
    _;
  }

  function addeatail1(string memory names) public Onlyowner
  {
    p.name=names;
  }  
  function addeatail2(uint age) public Onlyowner
  {
    p.age=age;
  }

  function addeatail3(string memory contact) public Onlyowner
  {
    p.contact=contact;
  }
  function addeatail4(string memory medic) public Onlyowner
  {
    p.medicine=medic;
  }
  function addeatail5(string memory predocs) public Onlyowner
  {
    p.predooc=predocs;
  }
  function addeatail6(string memory hos) public Onlyowner
  {
    p.hosp=hos;
  }
  function addeatail7(string memory med) public Onlyowner
  {
    p.med=med;
  }
  function addeatail8(uint NO) public Onlyowner
  {
    p.No_of_days=NO;
  }

  function display() public view Onlyowner returns(uint,string memory,string memory,string memory,string memory,string memory,string memory,uint)
  {
    return (p.age,p.name,p.contact,p.medicine,p.predooc,p.hosp,p.med,p.No_of_days);
  }
  
}
