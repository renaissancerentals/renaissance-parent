import re

def camel_to_snake(name):
    # Convert CamelCase to snake_case
    s1 = re.sub('(.)([A-Z][a-z]+)', r'\1_\2', name)
    snake = re.sub('([a-z0-9])([A-Z])', r'\1_\2', s1).lower()
    return snake

def pojo_to_record(java_code):
    # Extract class name
    class_name_match = re.search(r'public class (\w+)', java_code)
    class_name = class_name_match.group(1) if class_name_match else 'Unknown'

    # Use kebab-case for table name
    table_name = camel_to_snake(class_name)

    # Find fields with @Id annotation and their types and names
    field_blocks = re.findall(
        r'(@Id\s+)?(?:@[^\n]+\s+)*private\s+(final\s+)?([\w<>]+)\s+(\w+);',
        java_code)

    components = []
    for id_annot, final_mod, typ, name in field_blocks:
        component = ''
#         if id_annot:
#             component += '@Id '
        component += f'{typ} {name}'
        components.append(component)

    components_str = ', '.join(components)

    record_code = f'''@Builder
public record {class_name}({components_str}) {{
}}
'''
    return record_code

# Example usage
pojo_code = '''
public class JobVacancy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(max = 255, message = "allowed length up to {max}")
    @NotNull
    private String title;

    @NotNull
    private String description;


    @NotNull
    private LocalDateTime validThrough;

    @Enumerated(EnumType.STRING)
    @NotNull
    private EmploymentType employmentType;

    @NotNull
    private Float salary;

    @Enumerated(EnumType.STRING)
    @NotNull
    private SalaryType salaryType;

    @NotNull
    @Size(max = 255, message = "allowed length up to {max}")
    private String startDate;

    @NotNull
    @Size(max = 255, message = "allowed length up to {max}")
    private String workHours;

    @LastModifiedBy
    private String lastModifiedBy;

    @CreatedDate
    private LocalDate datePosted;

    @LastModifiedDate
    private LocalDateTime lastModifiedDate;

    private Boolean active;
}

'''

print(pojo_to_record(pojo_code))
