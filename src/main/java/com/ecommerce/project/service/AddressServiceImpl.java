package com.ecommerce.project.service;

import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.Address;
import com.ecommerce.project.model.User;
import com.ecommerce.project.payload.AddressDTO;
import com.ecommerce.project.repositories.AddressRepository;
import com.ecommerce.project.repositories.UserRepository;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressServiceImpl implements AddressService
{
    @Autowired
    ModelMapper modelMapper;

    @Autowired
    AddressRepository addressRepository;

    @Autowired
    UserRepository userRepository;

    @Override
    public AddressDTO createAddress(AddressDTO addressDTO, User user)
    {
        //Have Address Object
        //We have addressDTO and user so we convert addressDTO to address object
        Address address = modelMapper.map(addressDTO, Address.class);

        //Getting list of addresses from the user
        List<Address> addressList = user.getAddresses();

        //This will add the address to the list that exists within the user
        //adding the new address to existing list
        addressList.add(address);
        //updating the user object with the new list
        //because it is a bidirectional mapping
        user.setAddresses(addressList);

        //setting the user object within the address
        address.setUser(user);
        //Saved address after saving into the Repository
        Address savedAddress = addressRepository.save(address);

        return modelMapper.map(savedAddress, AddressDTO.class);
    }

    @Override
    public List<AddressDTO> getAddresses()
    {
        //finding all the addresses from the repository and save in address list.
        List<Address> addresses = addressRepository.findAll();
        //1.Convert this addresses into stream
        //2.Using Mapping we are mapping every object into the list  and into addressDTO.
        //collecting them back as a list and store as a list of address dtos.
        //here we have list of address so use stream and again back to list
       List<AddressDTO> addressDTOS =  addresses.stream()
                .map(address -> modelMapper.map(address, AddressDTO.class))
                //After mapping collect as a list
                .toList();
        return addressDTOS;
    }

    @Override
    public AddressDTO getAddressesById(Long addressId)
    {
        //Logic is to get address from the single user
        Address address = addressRepository.findById(addressId).
                orElseThrow(() -> new ResourceNotFoundException("Address", "addressId", addressId));

        //Map this address to DTO and return
        return modelMapper.map(address, AddressDTO.class);
    }

    @Override
    public List<AddressDTO> getUserAddresses(User user)
    {
        //make use of user and get the address
        List<Address> addresses = user.getAddresses();

       List<AddressDTO>  addressDTO = addresses.stream().
                map(address -> modelMapper.map(address,AddressDTO.class))
               .toList();
       return addressDTO;
    }

    @Override
    public AddressDTO updateAddress(Long addressId, @Valid AddressDTO addressDTO)
    {
        //Get address from Data base
        Address addressFromDB = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "addressId", addressId));

        //updating the address that we get from the user
        //getting from addressDTO and set into the addressFromDB
        addressFromDB.setCity(addressDTO.getCity());
        addressFromDB.setPincode(addressDTO.getPincode());
        addressFromDB.setStreet(addressDTO.getStreet());
        addressFromDB.setState(addressDTO.getState());
        addressFromDB.setCountry(addressDTO.getCountry());
        addressFromDB.setBuildingName(addressDTO.getBuildingName());

        //update addressFromDB into the DataBase
        Address updatedAddress = addressRepository.save(addressFromDB);

        //we update the data base
        //we need to update the user's address list
        //address list in the user

        //getting a user from the saved DB
        User user = addressFromDB.getUser();

        //This will remove the address object from the list if the address id of both of them match
        //The previous address is removed if they match the id
        user.getAddresses().removeIf(address -> address.getAddressId().equals(addressId));
        //updating the address which we received from the user i.e updatedAddress
        user.getAddresses().add(updatedAddress);

        //save the user into the user DB
        userRepository.save(user);
        //return the updated address DTO
        return modelMapper.map(updatedAddress, AddressDTO.class);
    }

    @Override
    public String deleteAddress(Long addressId)
    {
        Address addressFromDB = addressRepository.findById(addressId).
                orElseThrow(() -> new ResourceNotFoundException("Address", "addressId", addressId));
        //remove address from both the sides in user we delete address and from db also

        //remove Address from User
        User user = addressFromDB.getUser();
        //This will remove the address object from the list if the address id of both of them match
        //The previous address is removed if they match the id
        user.getAddresses().removeIf(address -> address.getAddressId().equals(addressId));
        //after deleting the user  save the user in user DB
        userRepository.save(user);

        //delete the user from the  address DB
        addressRepository.delete(addressFromDB);

        return "Address successfully deleted with addressId: " + addressId;
    }


}










//link to order many to one i.e one order can have many order items