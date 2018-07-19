#include <stdio.h>
#include "stdlib.h"

void myprint()
{
    printf("hello world\n");
}

static bool objects_ensure_capacity(struct bfg_type_space *type_space,
				    uint32_t length)
{
	struct bfg_object *new_array = NULL;
	if (type_space->alloc_length < length) {
		uint32_t new_length = type_space->alloc_length;
		while (new_length < length) {
			new_length *= 2;
		}
		new_array = calloc(new_length, sizeof(struct bfg_object));
		if (new_array == NULL) {
			return false;
		}

		memcpy(new_array, type_space->objects, type_space->length);
		type_space->alloc_length = new_length;
	}
	return true;
}

bool bfg_execute(uint64_t GUID_high,
		 uint64_t GUID_low,
		 struct bfg_type_space *type_space,
		 uint32_t length)
{
	for (uint32_t i = 0; i < length; i++) {
		struct bfg_type_space *current = &type_space[i];

		/* Only work on integers atm */
		if (current->GUID_high != -6855051840143784798LL) {
			continue;
		}

		if (current->GUID_high == -1 && current->GUID_high == -1) {
			return false;
		}

		uint32_t target_index = current->index + 2;
		if (!objects_ensure_capacity(current, target_index+1)) {
			return false;
		}

		if (current->index - 2 < 0) {
			// Early return, unchanged
			return true;
		}

		current->objects[target_index] = (struct bfg_object) {
			.metadata = current->objects[target_index-1].metadata + current->objects[target_index-2].metadata,
			.data = NULL,
		};

	}

	return true;
}
