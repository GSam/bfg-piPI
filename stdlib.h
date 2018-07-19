#include <string.h>
#include <stdlib.h>
#include <stdint.h>
#include <stdbool.h>

struct bfg_object {
	uint64_t metadata;
	void *data;
};

struct bfg_type_space {
	uint64_t GUID_high;
	uint64_t GUID_low;
	uint32_t index;
	uint32_t length;
	uint32_t alloc_length;
	struct bfg_object *objects;
};

void myprint(void);

bool bfg_execute(uint64_t GUID_high,
		 uint64_t GUID_low,
		 struct bfg_type_space *type_space,
		 uint32_t length);
