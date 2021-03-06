package core.batch.processor;

import org.springframework.batch.item.ItemProcessor;

import core.batch.DTO.EmployeeDTO;
import core.batch.model.Employee;

public class EmployeeProcessor implements ItemProcessor<Employee, EmployeeDTO> {

	@Override
    public EmployeeDTO process(final Employee employee) throws Exception {
        System.out.println("Transforming Employee(s) to EmployeeDTO(s)..");
        final EmployeeDTO empployeeDto = new EmployeeDTO(employee.getFirstName(), employee.getLastName(),
                employee.getCompanyName(), employee.getAddress(),employee.getCity(),employee.getCounty(),employee.getState()
        ,employee.getZip());

        return empployeeDto;
    }

}
